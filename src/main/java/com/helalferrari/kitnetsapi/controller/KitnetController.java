package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetRequestDTO;
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
import com.helalferrari.kitnetsapi.mapper.KitnetMapper;
import com.helalferrari.kitnetsapi.dto.kitnet.KitnetResponseDTO;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/kitnets")
public class KitnetController {

    // Garante que eles nunca sejam nulos após a construção.
    private final KitnetRepository kitnetRepository;
    private final KitnetMapper kitnetMapper;
    private final FileStorageService fileStorageService;

    // 2. Construtor Único (O Spring injeta tudo aqui automaticamente)
    public KitnetController(KitnetRepository kitnetRepository,
                            KitnetMapper kitnetMapper,
                            FileStorageService fileStorageService) {
        this.kitnetRepository = kitnetRepository;
        this.kitnetMapper = kitnetMapper;
        this.fileStorageService = fileStorageService;
    }

    // Dentro da classe KitnetController
    @PostMapping
    public Kitnet createKitnet(@RequestBody Kitnet kitnet) {
        return kitnetRepository.save(kitnet);
    }

    @GetMapping
    public List<Kitnet> getAllKitnets() {
        return kitnetRepository.findAll();
    }

    @GetMapping("/{id}") // O ID agora faz parte do caminho da URLgit
    public Optional<Kitnet> getKitnetById(@PathVariable Long id) {
        return kitnetRepository.findById(id);
    }

    @PutMapping("/{id}") // Mapeia para PUT /api/kitnets/{id}
    public Kitnet updateKitnet(@PathVariable Long id, @RequestBody Kitnet kitnetDetails) {

        // 1. Tenta encontrar a kitnet existente pelo ID
        return kitnetRepository.findById(id)
                .map(existingKitnet -> {
                    // 2. Se encontrada, atualiza os campos com os novos detalhes
                    // *ATENÇÃO*: Mantenha o ID original (o @RequestBody kitnetDetails não tem ID)
                    existingKitnet.setNome(kitnetDetails.getNome());
                    existingKitnet.setValor(kitnetDetails.getValor());
                    existingKitnet.setTaxa(kitnetDetails.getTaxa());
                    existingKitnet.setVagas(kitnetDetails.getVagas());
                    existingKitnet.setDescricao(kitnetDetails.getDescricao());

                    // 3. Salva a versão atualizada no banco de dados
                    return kitnetRepository.save(existingKitnet);
                })
                // Se findById não encontrar, lança a exceção que resulta em 404 NOT FOUND
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kitnet não encontrada com o ID: " + id));
    }

    @DeleteMapping("/{id}") // Mapeia para DELETE /api/kitnets/{id}
    // Retornamos void, pois o padrão 204 No Content não tem corpo de resposta.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKitnet(@PathVariable Long id) {

        // 1. Tenta encontrar o item primeiro
        if (kitnetRepository.existsById(id)) {
            // Se encontrar, exclui
            kitnetRepository.deleteById(id);
            // O Spring retorna automaticamente 204 No Content para métodos void
            // que não lançam exceção.
        } else {
            // Se não encontrar, lança 404 Not Found, assim como fizemos no PUT.
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kitnet não encontrada com o ID: " + id);
        }
    }

    @GetMapping("/search")
    public List<Kitnet> searchKitnets(
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max
    ) {
        // Se nenhum parâmetro for fornecido, retorna tudo.
        if (cep == null && min == null && max == null) {
            return kitnetRepository.findAll();
        }

        // 1. Define o valor mínimo (default 0) e máximo (default um valor alto, ex: 1000000)
        Double valorMin = (min != null) ? min : 0.0;
        Double valorMax = (max != null) ? max : 1000000.0;

        // 2. Define o termo de busca para o CEP. Se for nulo, usa string vazia para o LIKE
        String termoBusca = (cep != null) ? cep : "";

        // 3. Chama o método do Repository com todos os filtros
        return kitnetRepository.findByDescricaoContainingAndValorBetween(
                termoBusca,
                valorMin,
                valorMax
        );
    }

    // Método findById atualizado para usar o DTO
    @GetMapping("/{id}")
    public ResponseEntity<KitnetResponseDTO> findById(@PathVariable Long id) {
        return kitnetRepository.findById(id)
                .map(kitnetMapper::toResponseDTO) // AQUI: Usa o Mapper
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KitnetResponseDTO> create(
            @RequestPart("kitnet") KitnetRequestDTO requestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        // 1. Converte DTO -> Entidade
        Kitnet kitnetParaSalvar = kitnetMapper.toEntity(requestDTO);

        // 2. PRIMEIRA SALVADA: Persiste para gerar o ID do Landlord (se for novo)
        // O CascadeType.PERSIST garante que o Landlord também seja salvo/atualizado
        Kitnet kitnetSalva = kitnetRepository.save(kitnetParaSalvar);

        // Agora temos garantia que o ID existe!
        Long landlordId = kitnetSalva.getLandlord() != null ? kitnetSalva.getLandlord().getId() : null;

        // 3. Processa os arquivos (se houver) usando o ID recém-gerado
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // Passamos o ID para criar a subpasta correta: uploads/{id}/arquivo.jpg
                String relativePath = fileStorageService.save(file, landlordId);

                // Montamos a URL completa (ajuste conforme seu servidor de arquivos estáticos)
                String fileUrl = "/uploads/" + relativePath;

                // Criamos a Photo e vinculamos à Kitnet JÁ SALVA
                Photo photo = new Photo(null, fileUrl, kitnetSalva);
                kitnetSalva.addPhoto(photo);
            }

            // 4. SEGUNDA SALVADA: Atualiza a Kitnet no banco com a lista de fotos
            kitnetSalva = kitnetRepository.save(kitnetSalva);
        }

        // 5. Retorno
        KitnetResponseDTO responseDTO = kitnetMapper.toResponseDTO(kitnetSalva);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}