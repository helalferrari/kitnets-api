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
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.term() != null && !criteria.term().isBlank()) {
                String likePattern = "%" + criteria.term().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("description")), likePattern));
            }

            if (criteria.minValue() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("value"), criteria.minValue()));
            }

            if (criteria.maxValue() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("value"), criteria.maxValue()));
            }
            
            if (criteria.city() != null && !criteria.city().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), criteria.city().toLowerCase()));
            }
            
            if (criteria.neighborhood() != null && !criteria.neighborhood().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("neighborhood")), "%" + criteria.neighborhood().toLowerCase() + "%"));
            }

            if (criteria.furnished() != null) {
                predicates.add(cb.equal(root.get("furnished"), criteria.furnished()));
            }
            
            if (criteria.petsAllowed() != null) {
                predicates.add(cb.equal(root.get("petsAllowed"), criteria.petsAllowed()));
            }
            
            if (criteria.bathroomType() != null) {
                predicates.add(cb.equal(root.get("bathroomType"), criteria.bathroomType()));
            }

            if (criteria.amenities() != null && !criteria.amenities().isEmpty()) {
                for (Amenity amenity : criteria.amenities()) {
                    Join<Kitnet, Amenity> amenitiesJoin = root.join("amenities");
                    predicates.add(cb.equal(amenitiesJoin, amenity));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
