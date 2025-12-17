package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetRequestDTO;
import com.helalferrari.kitnetsapi.dto.kitnet.KitnetResponseDTO;
import com.helalferrari.kitnetsapi.mapper.KitnetMapper;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.Photo;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import com.helalferrari.kitnetsapi.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // Construtor Único (Injeção de Dependência)
    public KitnetController(KitnetRepository kitnetRepository,
                            KitnetMapper kitnetMapper,
                            FileStorageService fileStorageService) {
        this.kitnetRepository = kitnetRepository;
        this.kitnetMapper = kitnetMapper;
        this.fileStorageService = fileStorageService;
    }

    // --- MÉTODOS DE LEITURA (GET) ---

    @GetMapping
    public ResponseEntity<List<KitnetResponseDTO>> getAllKitnets() {
        // AJUSTE: Agora retorna DTOs para evitar loop infinito na listagem também
        List<KitnetResponseDTO> dtos = kitnetRepository.findAll().stream()
                .map(kitnetMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET POR ID
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

        // AJUSTE: Convertendo para DTO antes de devolver
        List<KitnetResponseDTO> dtos = kitnets.stream()
                .map(kitnetMapper::toResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // --- MÉTODOS DE ESCRITA (POST, PUT, DELETE) ---

    // CREATE (AJUSTADO PARA RETORNAR MAP SIMPLES E EVITAR LOOP)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> create(
            @RequestPart("kitnet") KitnetRequestDTO requestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        // 1. Converte DTO -> Entidade
        Kitnet kitnetParaSalvar = kitnetMapper.toEntity(requestDTO);

        // 2. PRIMEIRA SALVADA: Gera ID
        Kitnet kitnetSalva = kitnetRepository.save(kitnetParaSalvar);
        Long landlordId = kitnetSalva.getLandlord() != null ? kitnetSalva.getLandlord().getId() : null;

        // 3. Processa arquivos
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String relativePath = fileStorageService.save(file, landlordId);
                String fileUrl = "/uploads/" + relativePath; // URL relativa

                Photo photo = new Photo(null, fileUrl, kitnetSalva);
                kitnetSalva.addPhoto(photo);
            }
            // 4. SEGUNDA SALVADA: Atualiza com fotos
            kitnetSalva = kitnetRepository.save(kitnetSalva);
        }

        // 5. AJUSTE FINAL: Retorna um JSON simples para evitar Recursão Infinita
        Map<String, Object> response = new HashMap<>();
        response.put("id", kitnetSalva.getId());
        response.put("mensagem", "Kitnet cadastrada com sucesso!");

        // Retorna 201 Created com o corpo simples
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<KitnetResponseDTO> updateKitnet(@PathVariable Long id, @RequestBody Kitnet kitnetDetails) {
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