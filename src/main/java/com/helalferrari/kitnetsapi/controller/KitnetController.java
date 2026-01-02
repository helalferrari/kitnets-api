package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetRequestDTO;
import com.helalferrari.kitnetsapi.dto.kitnet.KitnetResponseDTO;
import com.helalferrari.kitnetsapi.mapper.KitnetMapper;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.Photo;
import com.helalferrari.kitnetsapi.model.User;
import com.helalferrari.kitnetsapi.model.enums.UserRole; // Import do Enum
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import com.helalferrari.kitnetsapi.repository.UserRepository;
import com.helalferrari.kitnetsapi.repository.spec.KitnetSpecification;
import com.helalferrari.kitnetsapi.service.FileStorageService;
import com.helalferrari.kitnetsapi.service.GroqService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Novo Import
import org.springframework.security.core.context.SecurityContextHolder; // Novo Import
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kitnets")
public class KitnetController {

    private final KitnetRepository kitnetRepository;
    private final KitnetMapper kitnetMapper;
    private final FileStorageService fileStorageService;
    private final GroqService groqService;
    // O UserRepository não é mais estritamente necessário no create,
    // mas pode ser mantido se for usado em outros métodos futuros.
    private final UserRepository userRepository;

    public KitnetController(KitnetRepository kitnetRepository,
                            KitnetMapper kitnetMapper,
                            FileStorageService fileStorageService,
                            UserRepository userRepository,
                            GroqService groqService) {
        this.kitnetRepository = kitnetRepository;
        this.kitnetMapper = kitnetMapper;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.groqService = groqService;
    }

    // --- MÉTODOS DE LEITURA (GET) ---

    @GetMapping
    public ResponseEntity<List<KitnetResponseDTO>> getAllKitnets() {
        List<KitnetResponseDTO> dtos = kitnetRepository.findAll().stream()
                .map(kitnetMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitnetResponseDTO> findById(@PathVariable Long id) {
        return kitnetRepository.findById(id)
                .map(kitnetMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/ai")
    public ResponseEntity<List<KitnetResponseDTO>> searchByAI(@RequestParam String query) {
        var criteria = groqService.extractSearchCriteria(query);
        var spec = KitnetSpecification.fromCriteria(criteria);

        List<KitnetResponseDTO> dtos = kitnetRepository.findAll(spec).stream()
                .map(kitnetMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<KitnetResponseDTO>> searchKitnets(
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max
    ) {
        List<Kitnet> kitnets;

        if (cep == null && min == null && max == null) {
            kitnets = kitnetRepository.findAll();
        } else {
            Double minValue = (min != null) ? min : 0.0;
            Double maxValue = (max != null) ? max : 1000000.0;
            String searchTerm = (cep != null) ? cep : "";
            kitnets = kitnetRepository.findByDescriptionContainingAndValueBetween(searchTerm, minValue, maxValue);
        }

        List<KitnetResponseDTO> dtos = kitnets.stream()
                .map(kitnetMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/my-kitnets")
    public ResponseEntity<List<KitnetResponseDTO>> getMyKitnets() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        List<KitnetResponseDTO> dtos = kitnetRepository.findByUser(currentUser).stream()
                .map(kitnetMapper::toResponseDTO)
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // --- MÉTODOS DE ESCRITA (POST, PUT, DELETE) ---

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> create(
            @RequestPart("kitnet") KitnetRequestDTO requestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        // 1. Recupera o Usuário Logado do Contexto de Segurança
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // 2. Validação de Segurança (Opcional, mas recomendada):
        // Garante que apenas LANDLORD pode cadastrar, mesmo que o front deixe passar.
        if (currentUser.getRole() != UserRole.LANDLORD && currentUser.getRole() != UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas proprietários podem cadastrar kitnets.");
        }

        // 3. Converte DTO -> Entidade
        Kitnet kitnetParaSalvar = kitnetMapper.toEntity(requestDTO);

        // 4. Associa a Kitnet ao Usuário Logado (Vínculo Implícito)
        kitnetParaSalvar.setUser(currentUser);

        // 5. PRIMEIRA SALVADA: Gera ID da Kitnet
        Kitnet kitnetSalva = kitnetRepository.save(kitnetParaSalvar);

        // 6. Processa arquivos (Usando ID do Usuário Logado para organizar pastas)
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // Usa currentUser.getId()
                String relativePath = fileStorageService.save(file, currentUser.getId());
                String fileUrl = "/uploads/" + relativePath;

                Photo photo = new Photo(null, fileUrl, kitnetSalva);
                kitnetSalva.addPhoto(photo);
            }
            // 7. SEGUNDA SALVADA: Atualiza com fotos
            kitnetSalva = kitnetRepository.save(kitnetSalva);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", kitnetSalva.getId());
        response.put("mensagem", "Kitnet cadastrada com sucesso!");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KitnetResponseDTO> updateKitnet(@PathVariable Long id, @RequestBody Kitnet kitnetDetails) {
        // Nota: Aqui seria ideal verificar se o usuário logado é DONO dessa kitnet antes de deixar editar
        return kitnetRepository.findById(id)
                .map(existingKitnet -> {
                    existingKitnet.setName(kitnetDetails.getName());
                    existingKitnet.setValue(kitnetDetails.getValue());
                    existingKitnet.setFee(kitnetDetails.getFee());
                    existingKitnet.setParkingSpaces(kitnetDetails.getParkingSpaces());
                    existingKitnet.setDescription(kitnetDetails.getDescription());
                    
                    existingKitnet.setArea(kitnetDetails.getArea());
                    existingKitnet.setFurnished(kitnetDetails.getFurnished());
                    existingKitnet.setPetsAllowed(kitnetDetails.getPetsAllowed());
                    existingKitnet.setBathroomType(kitnetDetails.getBathroomType());
                    existingKitnet.setAmenities(kitnetDetails.getAmenities());
                    
                    // Atualização dos campos de endereço
                    existingKitnet.setCep(kitnetDetails.getCep());
                    existingKitnet.setStreet(kitnetDetails.getStreet());
                    existingKitnet.setComplement(kitnetDetails.getComplement());
                    existingKitnet.setNumber(kitnetDetails.getNumber());
                    existingKitnet.setNeighborhood(kitnetDetails.getNeighborhood());
                    existingKitnet.setCity(kitnetDetails.getCity());
                    existingKitnet.setState(kitnetDetails.getState());
                    existingKitnet.setIbge(kitnetDetails.getIbge());
                    existingKitnet.setLongitude(kitnetDetails.getLongitude());
                    existingKitnet.setLatitude(kitnetDetails.getLatitude());

                    Kitnet updated = kitnetRepository.save(existingKitnet);
                    return ResponseEntity.ok(kitnetMapper.toResponseDTO(updated));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kitnet não encontrada: " + id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKitnet(@PathVariable Long id) {
        if (kitnetRepository.existsById(id)) {
            kitnetRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kitnet não encontrada: " + id);
        }
    }
}