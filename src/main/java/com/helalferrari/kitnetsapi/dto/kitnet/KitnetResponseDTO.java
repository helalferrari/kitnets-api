package com.helalferrari.kitnetsapi.dto.kitnet;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String cep;
    private String logradouro;
    private String complement;
    private String number;
    private String neighborhood;
    private String city;
    private String state;
    private String ibge;
    
    @JsonProperty("long")
    private String longitude;
    
    @JsonProperty("lat")
    private String latitude;
    
    private OwnerDTO landlord;
    private List<PhotoDTO> photos;
}