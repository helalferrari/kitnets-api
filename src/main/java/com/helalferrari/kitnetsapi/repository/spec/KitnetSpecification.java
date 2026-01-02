package com.helalferrari.kitnetsapi.repository.spec;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetSearchCriteriaDTO;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.enums.Amenity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class KitnetSpecification {

    private KitnetSpecification() {}

    public static Specification<Kitnet> fromCriteria(KitnetSearchCriteriaDTO criteria) {
        return Specification.where(byTerm(criteria.term()))
                .and(byPriceRange(criteria.minValue(), criteria.maxValue()))
                .and(byLocation(criteria.city(), criteria.neighborhood()))
                .and(byAttributes(criteria.furnished(), criteria.petsAllowed(), criteria.bathroomType()))
                .and(byAmenities(criteria.amenities()));
    }

    private static Specification<Kitnet> byTerm(String term) {
        return (root, query, cb) -> {
            if (term == null || term.isBlank()) {
                return null;
            }
            String likePattern = "%" + term.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("description")), likePattern);
        };
    }

    private static Specification<Kitnet> byPriceRange(Double min, Double max) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("value"), min));
            }
            if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("value"), max));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Kitnet> byLocation(String city, String neighborhood) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
            }
            if (neighborhood != null && !neighborhood.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("neighborhood")), "%" + neighborhood.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Kitnet> byAttributes(Boolean furnished, Boolean petsAllowed, com.helalferrari.kitnetsapi.model.enums.BathroomType bathroomType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (furnished != null) {
                predicates.add(cb.equal(root.get("furnished"), furnished));
            }
            if (petsAllowed != null) {
                predicates.add(cb.equal(root.get("petsAllowed"), petsAllowed));
            }
            if (bathroomType != null) {
                predicates.add(cb.equal(root.get("bathroomType"), bathroomType));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Specification<Kitnet> byAmenities(java.util.Set<Amenity> amenities) {
        return (root, query, cb) -> {
            if (amenities == null || amenities.isEmpty()) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();
            for (Amenity amenity : amenities) {
                Join<Kitnet, Amenity> amenitiesJoin = root.join("amenities");
                predicates.add(cb.equal(amenitiesJoin, amenity));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
