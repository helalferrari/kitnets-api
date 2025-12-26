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
import com.helalferrari.kitnetsapi.service.FileStorageService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kitnets")
public class KitnetController {

    private final KitnetRepository kitnetRepository;
    private final KitnetMapper kitnetMapper;
    private final FileStorageService fileStorageService;
    // O UserRepository não é mais estritamente necessário no create,
    // mas pode ser mantido se for usado em outros métodos futuros.
    private final UserRepository userRepository;

    public KitnetController(KitnetRepository kitnetRepository,
                            KitnetMapper kitnetMapper,
                            FileStorageService fileStorageService,
                            UserRepository userRepository) {
        this.kitnetRepository = kitnetRepository;
        this.kitnetMapper = kitnetMapper;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
    }

    // --- MÉTODOS DE LEITURA (GET) ---

    @GetMapping
    public ResponseEntity<List<KitnetResponseDTO>> getAllKitnets() {
        List<KitnetResponseDTO> dtos = kitnetRepository.findAll().stream()
                .map(kitnetMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<KitnetResponseDTO> findById(@PathVariable Long id) {
        return kitnetRepository.findById(id)
                .map(kitnetMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
            Double valorMin = (min != null) ? min : 0.0;
            Double valorMax = (max != null) ? max : 1000000.0;
            String termoBusca = (cep != null) ? cep : "";
            kitnets = kitnetRepository.findByDescricaoContainingAndValorBetween(termoBusca, valorMin, valorMax);
        }

        List<KitnetResponseDTO> dtos = kitnets.stream()
                .map(kitnetMapper::toResponseDTO)
                .collect(Collectors.toList());

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
                    existingKitnet.setNome(kitnetDetails.getNome());
                    existingKitnet.setValor(kitnetDetails.getValor());
                    existingKitnet.setTaxa(kitnetDetails.getTaxa());
                    existingKitnet.setVagas(kitnetDetails.getVagas());
                    existingKitnet.setDescricao(kitnetDetails.getDescricao());

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