package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired; // Importação necessária
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
// Definimos o path base para este Controller, seguindo o padrão REST.
@RequestMapping("/api/kitnets")
public class KitnetController {

    @Autowired // injeta automaticamente o Repositório
    private KitnetRepository kitnetRepository;

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
    public Optional<Kitnet> getKitnetById(@PathVariable Integer id) {
        return kitnetRepository.findById(id);
    }

    @PutMapping("/{id}") // Mapeia para PUT /api/kitnets/{id}
    public Kitnet updateKitnet(@PathVariable Integer id, @RequestBody Kitnet kitnetDetails) {

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
    public void deleteKitnet(@PathVariable Integer id) {

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
}