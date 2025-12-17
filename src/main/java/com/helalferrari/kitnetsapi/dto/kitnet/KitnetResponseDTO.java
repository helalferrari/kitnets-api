package com.helalferrari.kitnetsapi.dto.kitnet; // Crie este pacote

import lombok.Data;
import java.util.List;

@Data
public class KitnetResponseDTO {
    private Long id;
    private String nome;
    private Double valor;
    private Integer vagas;
    private Double taxa;
    private String descricao;

    // Novas informações para o frontend
    private LandlordDTO landlord; // Usaremos um DTO aninhado
    private List<PhotoDTO> photos;
}