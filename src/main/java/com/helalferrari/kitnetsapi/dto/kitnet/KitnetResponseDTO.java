package com.helalferrari.kitnetsapi.dto.kitnet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helalferrari.kitnetsapi.model.enums.Amenity;
import com.helalferrari.kitnetsapi.model.enums.BathroomType;
import lombok.Data;
import java.util.List;
import java.util.Set;
// ADICIONE ESTE IMPORT:
import com.helalferrari.kitnetsapi.dto.auth.OwnerDTO;

@Data
public class KitnetResponseDTO {
    private Long id;
    private String name;
    private Double value;
    private Integer parkingSpaces;
    private Double fee;
    private String description;
    
    private Double area;
    private Boolean furnished;
    private Boolean petsAllowed;
    private BathroomType bathroomType;
    private Set<Amenity> amenities;

    private String cep;
    private String street;
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