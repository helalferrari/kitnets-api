package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired; // Importação necessária

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
}