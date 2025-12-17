package com.helalferrari.kitnetsapi.dto.kitnet;

import lombok.Data;

@Data
public class LandlordDTO {
    private Long id;
    private String name;
    private String email;
    // Note que o telefone pode ser omitido se for considerado privado.
}