package com.helalferrari.kitnetsapi.dto.kitnet;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KitnetRequestDTO(
        String nome,
        Double valor,
        Integer vagas,
        Double taxa,
        String descricao,
        Long userId,
        String cep,
        String logradouro,
        String complement,
        String number,
        String neighborhood,
        String city,
        String state,
        String ibge,
        @JsonProperty("long") String longitude,
        @JsonProperty("lat") String latitude
) {}