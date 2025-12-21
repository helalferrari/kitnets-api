package com.helalferrari.kitnetsapi.dto.kitnet;

public record KitnetRequestDTO(
        String nome,
        Double valor,
        Integer vagas,
        Double taxa,
        String descricao,
        Long userId
) {}