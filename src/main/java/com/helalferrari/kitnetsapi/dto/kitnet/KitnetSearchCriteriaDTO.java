package com.helalferrari.kitnetsapi.dto.kitnet;

import com.helalferrari.kitnetsapi.model.enums.Amenity;
import com.helalferrari.kitnetsapi.model.enums.BathroomType;
import java.util.Set;

public record KitnetSearchCriteriaDTO(
        String term,
        Double minValue,
        Double maxValue,
        Set<Amenity> amenities,
        String city,
        String neighborhood,
        Boolean furnished,
        Boolean petsAllowed,
        BathroomType bathroomType
) {}
