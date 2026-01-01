package com.helalferrari.kitnetsapi.dto.kitnet;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KitnetRequestDTOTest {

    @Test
    @DisplayName("Should create KitnetRequestDTO with correct values")
    void shouldCreateKitnetRequestDTO() {
        // Arrange
        String nome = "Kitnet Beira Mar";
        Double valor = 1200.50;
        Integer vagas = 1;
        Double taxa = 50.0;
        String descricao = "Vista para o mar";
        Long userId = 10L;
        String cep = "01001-000";
        String logradouro = "Praça da Sé";
        String complement = "lado ímpar";
        String number = "100";
        String neighborhood = "Sé";
        String city = "São Paulo";
        String state = "SP";
        String ibge = "3550308";
        String longitude = "-49.0629788";
        String latitude = "-26.9244749";
        Boolean nonumber = false;


        // Act
        KitnetRequestDTO dto = new KitnetRequestDTO(nome, valor, vagas, taxa, descricao, userId, cep, logradouro, complement, number, neighborhood, city, state, ibge, longitude, latitude, nonumber);

        // Assert
        assertNotNull(dto);
        // Ordem Correta: (Esperado, Atual)
        assertEquals(nome, dto.nome());
        assertEquals(valor, dto.valor());
        assertEquals(vagas, dto.vagas());
        assertEquals(taxa, dto.taxa());
        assertEquals(descricao, dto.descricao());
        assertEquals(userId, dto.userId());
        assertEquals(cep, dto.cep());
        assertEquals(logradouro, dto.logradouro());
        assertEquals(complement, dto.complement());
        assertEquals(number, dto.number());
        assertEquals(neighborhood, dto.neighborhood());
        assertEquals(city, dto.city());
        assertEquals(state, dto.state());
        assertEquals(ibge, dto.ibge());
        assertEquals(longitude, dto.longitude());
        assertEquals(latitude, dto.latitude());
        assertEquals(nonumber, dto.nonumber());
    }

    @Test
    @DisplayName("Should test equals and hashCode behavior")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        KitnetRequestDTO dto1 = new KitnetRequestDTO("K1", 100.0, 1, 10.0, "D1", 1L, "cep", "log", "comp", "1", "neigh", "city", "st", "ibge", "long", "lat", false);
        KitnetRequestDTO dto2 = new KitnetRequestDTO("K1", 100.0, 1, 10.0, "D1", 1L, "cep", "log", "comp", "1", "neigh", "city", "st", "ibge", "long", "lat", false); // Igual
        KitnetRequestDTO dto3 = new KitnetRequestDTO("K2", 200.0, 2, 20.0, "D2", 2L, "cep", "log", "comp", "2", "neigh", "city", "st", "ibge", "long", "lat", false); // Diferente

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
        KitnetRequestDTO dto = new KitnetRequestDTO("Kitnet String", 500.0, 0, 0.0, "Desc", 5L, "cep", "log", "comp", "1", "neigh", "city", "st", "ibge", "long", "lat", false);

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