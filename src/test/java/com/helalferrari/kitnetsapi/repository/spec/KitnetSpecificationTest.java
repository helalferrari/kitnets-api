package com.helalferrari.kitnetsapi.repository.spec;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetSearchCriteriaDTO;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.enums.Amenity;
import com.helalferrari.kitnetsapi.model.enums.BathroomType;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class KitnetSpecificationTest {

    @Autowired
    private KitnetRepository kitnetRepository;

    @BeforeEach
    void setup() {
        kitnetRepository.deleteAll();
        
        Kitnet k1 = new Kitnet();
        k1.setName("K1");
        k1.setDescription("Kitnet com vista mar");
        k1.setValue(1000.0);
        k1.setCity("Florianópolis");
        k1.setNeighborhood("Trindade");
        k1.setFurnished(true);
        k1.setPetsAllowed(false);
        k1.setBathroomType(BathroomType.PRIVATIVO);
        Set<Amenity> a1 = new HashSet<>();
        a1.add(Amenity.WIFI);
        a1.add(Amenity.AR_CONDICIONADO);
        k1.setAmenities(a1);
        
        Kitnet k2 = new Kitnet();
        k2.setName("K2");
        k2.setDescription("Kitnet simples no centro");
        k2.setValue(500.0);
        k2.setCity("São Paulo");
        k2.setNeighborhood("Centro");
        k2.setFurnished(false);
        k2.setPetsAllowed(true);
        k2.setBathroomType(BathroomType.COMPARTILHADO);
        Set<Amenity> a2 = new HashSet<>();
        a2.add(Amenity.WIFI);
        k2.setAmenities(a2);
        
        kitnetRepository.save(k1);
        kitnetRepository.save(k2);
    }

    @Test
    @DisplayName("Should filter by term (description)")
    void shouldFilterByTerm() {
        var criteria = new KitnetSearchCriteriaDTO("vista", null, null, null, null, null, null, null, null);
        List<Kitnet> result = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteria));
        assertEquals(1, result.size());
        assertEquals("K1", result.get(0).getName());
    }

    @Test
    @DisplayName("Should filter by price range")
    void shouldFilterByPriceRange() {
        // Min value
        var criteriaMin = new KitnetSearchCriteriaDTO(null, 800.0, null, null, null, null, null, null, null);
        List<Kitnet> resultMin = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaMin));
        assertEquals(1, resultMin.size());
        assertEquals("K1", resultMin.get(0).getName());

        // Max value
        var criteriaMax = new KitnetSearchCriteriaDTO(null, null, 600.0, null, null, null, null, null, null);
        List<Kitnet> resultMax = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaMax));
        assertEquals(1, resultMax.size());
        assertEquals("K2", resultMax.get(0).getName());
        
        // Between
        var criteriaBetween = new KitnetSearchCriteriaDTO(null, 400.0, 1100.0, null, null, null, null, null, null);
        List<Kitnet> resultBetween = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaBetween));
        assertEquals(2, resultBetween.size());
    }

    @Test
    @DisplayName("Should filter by location (city/neighborhood)")
    void shouldFilterByLocation() {
        // City (Exact match logic in spec)
        var criteriaCity = new KitnetSearchCriteriaDTO(null, null, null, null, "Florianópolis", null, null, null, null);
        List<Kitnet> resultCity = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaCity));
        assertEquals(1, resultCity.size());
        assertEquals("K1", resultCity.get(0).getName());

        // Neighborhood (Partial match logic in spec)
        var criteriaNeigh = new KitnetSearchCriteriaDTO(null, null, null, null, null, "rin", null, null, null); // matches Trindade
        List<Kitnet> resultNeigh = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaNeigh));
        assertEquals(1, resultNeigh.size());
        assertEquals("K1", resultNeigh.get(0).getName());
    }

    @Test
    @DisplayName("Should filter by boolean flags and enum")
    void shouldFilterByBooleansAndEnum() {
        // Furnished
        var criteriaFurnished = new KitnetSearchCriteriaDTO(null, null, null, null, null, null, true, null, null);
        List<Kitnet> resultFurn = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaFurnished));
        assertEquals(1, resultFurn.size());
        assertEquals("K1", resultFurn.get(0).getName());

        // Pets
        var criteriaPets = new KitnetSearchCriteriaDTO(null, null, null, null, null, null, null, true, null);
        List<Kitnet> resultPets = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaPets));
        assertEquals(1, resultPets.size());
        assertEquals("K2", resultPets.get(0).getName());
        
        // Bathroom
        var criteriaBath = new KitnetSearchCriteriaDTO(null, null, null, null, null, null, null, null, BathroomType.COMPARTILHADO);
        List<Kitnet> resultBath = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaBath));
        assertEquals(1, resultBath.size());
        assertEquals("K2", resultBath.get(0).getName());
    }

    @Test
    @DisplayName("Should filter by amenities")
    void shouldFilterByAmenities() {
        // Single amenity (both have WIFI)
        Set<Amenity> wifi = new HashSet<>();
        wifi.add(Amenity.WIFI);
        var criteriaWifi = new KitnetSearchCriteriaDTO(null, null, null, wifi, null, null, null, null, null);
        List<Kitnet> resultWifi = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaWifi));
        assertEquals(2, resultWifi.size());

        // Multiple amenities (only K1 has AC)
        Set<Amenity> wifiAndAc = new HashSet<>();
        wifiAndAc.add(Amenity.WIFI);
        wifiAndAc.add(Amenity.AR_CONDICIONADO);
        var criteriaAc = new KitnetSearchCriteriaDTO(null, null, null, wifiAndAc, null, null, null, null, null);
        List<Kitnet> resultAc = kitnetRepository.findAll(KitnetSpecification.fromCriteria(criteriaAc));
        assertEquals(1, resultAc.size());
        assertEquals("K1", resultAc.get(0).getName());
    }
}
