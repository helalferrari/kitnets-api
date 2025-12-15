package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired; // Importação necessária

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
}