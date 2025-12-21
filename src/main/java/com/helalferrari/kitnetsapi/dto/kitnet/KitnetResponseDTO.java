package com.helalferrari.kitnetsapi.dto.kitnet;

import lombok.Data;
import java.util.List;
// ADICIONE ESTE IMPORT:
import com.helalferrari.kitnetsapi.dto.auth.OwnerDTO;

@Data
public class KitnetResponseDTO {
    private Long id;
    private String nome;
    private Double valor;
    private Integer vagas;
    private Double taxa;
    private String descricao;

    private OwnerDTO landlord;
    private List<PhotoDTO> photos;
}