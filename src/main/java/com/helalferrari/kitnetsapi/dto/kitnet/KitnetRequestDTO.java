package com.helalferrari.kitnetsapi.dto.kitnet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.helalferrari.kitnetsapi.model.enums.Amenity;
import com.helalferrari.kitnetsapi.model.enums.BathroomType;
import java.util.Set;

public record KitnetRequestDTO(
        String name,
        Double value,
        Integer parkingSpaces,
        Double fee,
        String description,
        Double area,
        Boolean furnished,
        Boolean petsAllowed,
        BathroomType bathroomType,
        Set<Amenity> amenities,
        Long userId,
        String cep,
        String street,
        String complement,
        String number,
        String neighborhood,
        String city,
        String state,
        String ibge,
        @JsonProperty("long") String longitude,
        @JsonProperty("lat") String latitude
) {}