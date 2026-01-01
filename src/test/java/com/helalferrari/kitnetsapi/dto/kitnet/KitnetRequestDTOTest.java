package com.helalferrari.kitnetsapi.dto.kitnet;

import com.helalferrari.kitnetsapi.model.enums.Amenity;
import com.helalferrari.kitnetsapi.model.enums.BathroomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class KitnetRequestDTOTest {

    @Test
    @DisplayName("Should create KitnetRequestDTO with correct values")
    void shouldCreateKitnetRequestDTO() {
        // Arrange
        String name = "Kitnet Beira Mar";
        Double value = 1200.50;
        Integer parkingSpaces = 1;
        Double fee = 50.0;
        String description = "Vista para o mar";
        Long userId = 10L;
        String cep = "01001-000";
        String street = "Praça da Sé";
        String complement = "lado ímpar";
        String number = "100";
        String neighborhood = "Sé";
        String city = "São Paulo";
        String state = "SP";
        String ibge = "3550308";
        String longitude = "-49.0629788";
        String latitude = "-26.9244749";
        
        Double area = 30.0;
        Boolean furnished = true;
        Boolean petsAllowed = false;
        BathroomType bathroomType = BathroomType.PRIVATIVO;
        HashSet<Amenity> amenities = new HashSet<>();


        // Act
        KitnetRequestDTO dto = new KitnetRequestDTO(name, value, parkingSpaces, fee, description, area, furnished, petsAllowed, bathroomType, amenities, userId, cep, street, complement, number, neighborhood, city, state, ibge, longitude, latitude);

        // Assert
        assertNotNull(dto);
        // Ordem Correta: (Esperado, Atual)
        assertEquals(name, dto.name());
        assertEquals(value, dto.value());
        assertEquals(parkingSpaces, dto.parkingSpaces());
        assertEquals(fee, dto.fee());
        assertEquals(description, dto.description());
        assertEquals(area, dto.area());
        assertEquals(furnished, dto.furnished());
        assertEquals(petsAllowed, dto.petsAllowed());
        assertEquals(bathroomType, dto.bathroomType());
        assertEquals(amenities, dto.amenities());
        assertEquals(userId, dto.userId());
        assertEquals(cep, dto.cep());
        assertEquals(street, dto.street());
        assertEquals(complement, dto.complement());
        assertEquals(number, dto.number());
        assertEquals(neighborhood, dto.neighborhood());
        assertEquals(city, dto.city());
        assertEquals(state, dto.state());
        assertEquals(ibge, dto.ibge());
        assertEquals(longitude, dto.longitude());
        assertEquals(latitude, dto.latitude());
    }

    @Test
    @DisplayName("Should test equals and hashCode behavior")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        KitnetRequestDTO dto1 = new KitnetRequestDTO("K1", 100.0, 1, 10.0, "D1", 20.0, false, false, BathroomType.COMPARTILHADO, new HashSet<>(), 1L, "cep", "st", "comp", "1", "neigh", "city", "st", "ibge", "long", "lat");
        KitnetRequestDTO dto2 = new KitnetRequestDTO("K1", 100.0, 1, 10.0, "D1", 20.0, false, false, BathroomType.COMPARTILHADO, new HashSet<>(), 1L, "cep", "st", "comp", "1", "neigh", "city", "st", "ibge", "long", "lat"); // Igual
        KitnetRequestDTO dto3 = new KitnetRequestDTO("K2", 200.0, 2, 20.0, "D2", 30.0, true, true, BathroomType.PRIVATIVO, new HashSet<>(), 2L, "cep", "st", "comp", "2", "neigh", "city", "st", "ibge", "long", "lat"); // Diferente

        // Assert Equals
        // Correção do Sonar: O primeiro argumento é sempre a referência (o esperado)
        assertEquals(dto2, dto1);

        assertNotEquals(dto3, dto1);

        // Aqui era onde o Sonar estava reclamando mais:
        assertNotEquals(null, dto1);
        assertNotEquals(new Object(), dto1);

        // Assert HashCode
        assertEquals(dto2.hashCode(), dto1.hashCode());
        assertNotEquals(dto3.hashCode(), dto1.hashCode());
    }

    @Test
    @DisplayName("Should return correct String representation")
    void shouldReturnCorrectToString() {
        // Arrange
        KitnetRequestDTO dto = new KitnetRequestDTO("Kitnet String", 500.0, 0, 0.0, "Desc", 20.0, false, false, BathroomType.COMPARTILHADO, new HashSet<>(), 5L, "cep", "st", "comp", "1", "neigh", "city", "st", "ibge", "long", "lat");

        // Act
        String stringResult = dto.toString();

        // Assert
        assertTrue(stringResult.contains("KitnetRequestDTO"));
        assertTrue(stringResult.contains("Kitnet String"));
        assertTrue(stringResult.contains("500.0"));
        assertTrue(stringResult.contains("userId=5"));
        assertTrue(stringResult.contains("cep"));
    }
}