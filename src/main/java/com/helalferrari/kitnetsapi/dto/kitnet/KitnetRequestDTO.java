package com.helalferrari.kitnetsapi.dto.kitnet;

import lombok.Data;
import java.util.List;

@Data
public class KitnetRequestDTO {
    // Não precisa de ID (é gerado pelo banco)
    private String nome;
    private Double valor;
    private Integer vagas;
    private Double taxa;
    private String descricao;

    // Recebemos o objeto completo ou apenas o ID do proprietário?
    private LandlordDTO landlord;

    // Lista de URLs das fotos
    private List<PhotoDTO> photos;
}