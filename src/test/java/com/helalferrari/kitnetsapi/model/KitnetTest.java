package com.helalferrari.kitnetsapi.model;

import com.helalferrari.kitnetsapi.model.enums.BathroomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class KitnetTest {

    @Test
    @DisplayName("Should create Kitnet with default values")
    void shouldCreateKitnetWithDefaultValues() {
        // Act
        Kitnet kitnet = new Kitnet();

        // Assert
        assertNotNull(kitnet.getPhotos());
        assertTrue(kitnet.getPhotos().isEmpty());
        assertNotNull(kitnet.getCreatedAt());
    }

    @Test
    @DisplayName("Should add photo and update bidirectional relationship")
    void shouldAddPhoto() {
        // Arrange
        Kitnet kitnet = new Kitnet();
        Photo photoMock = Mockito.mock(Photo.class);

        // Act
        kitnet.addPhoto(photoMock);

        // Assert
        assertEquals(1, kitnet.getPhotos().size());
        assertTrue(kitnet.getPhotos().contains(photoMock));

        Mockito.verify(photoMock).setKitnet(kitnet);
    }

    @Test
    @DisplayName("Should remove photo and update bidirectional relationship")
    void shouldRemovePhoto() {
        // Arrange
        Kitnet kitnet = new Kitnet();
        Photo photoMock = Mockito.mock(Photo.class);

        kitnet.getPhotos().add(photoMock);

        // Act
        kitnet.removePhoto(photoMock);

        // Assert
        assertTrue(kitnet.getPhotos().isEmpty());
        Mockito.verify(photoMock).setKitnet(null);
    }

    @Test
    @DisplayName("Should test AllArgsConstructor and Getters")
    void shouldTestAllArgsConstructor() {
        // Arrange
        Long id = 1L;
        String nome = "Kitnet Centro";
        Double valor = 1000.0;
        User userMock = Mockito.mock(User.class);
        LocalDateTime now = LocalDateTime.now();

        // Act
        Kitnet kitnet = new Kitnet(
                id,
                nome,
                valor,
                0.0,
                1,
                "Desc",
                50.0, // area
                true, // mobiliada
                false, // aceitaAnimais
                BathroomType.PRIVATIVO, // tipoBanheiro
                new HashSet<>(), // comodidades
                now,
                now.plusDays(30),
                "00000-000", // cep
                "Log", // logradouro
                "Comp", // complement
                "123", // number
                "Neigh", // neighborhood
                "City", // city
                "UF", // state
                "1234567", // ibge
                "1.0", // longitude
                "2.0", // latitude
                userMock,
                new ArrayList<>()
        );

        // Assert
        assertEquals(id, kitnet.getId());
        assertEquals(nome, kitnet.getName());
        assertEquals(valor, kitnet.getValue());
        assertEquals(userMock, kitnet.getUser());
    }

    @Test
    @DisplayName("Should set number to S/N when null or blank on PrePersist/PreUpdate")
    void shouldSetNumberToSNWhenNullOrBlank() {
        // Arrange
        Kitnet kitnet = new Kitnet();
        kitnet.setNumber(null);

        // Act
        kitnet.prePersist();

        // Assert
        assertEquals("S/N", kitnet.getNumber());

        // Arrange 2 (Blank)
        kitnet.setNumber("   ");
        
        // Act 2
        kitnet.prePersist();
        
        // Assert 2
        assertEquals("S/N", kitnet.getNumber());
        
        // Arrange 3 (Valid Number)
        kitnet.setNumber("123");
        
        // Act 3
        kitnet.prePersist();
        
        // Assert 3
        assertEquals("123", kitnet.getNumber());
    }

    @Test
    @DisplayName("Should test Lombok generated methods (Equals, HashCode, ToString)")
    void shouldTestLombokMethods() {
        // --- CORREÇÃO AQUI ---
        // Criamos uma data fixa para garantir que ambos os objetos tenham EXATAMENTE o mesmo nanossegundo
        LocalDateTime fixedDate = LocalDateTime.now();

        // Arrange Object 1
        Kitnet kitnet1 = new Kitnet();
        kitnet1.setId(1L);
        kitnet1.setName("Kitnet A");
        kitnet1.setCreatedAt(fixedDate); // Forçamos a data

        // Arrange Object 2
        Kitnet kitnet2 = new Kitnet();
        kitnet2.setId(1L);
        kitnet2.setName("Kitnet A");
        kitnet2.setCreatedAt(fixedDate); // Forçamos a mesma data

        // Arrange Object 3 (Diferente)
        Kitnet kitnet3 = new Kitnet();
        kitnet3.setId(2L);
        kitnet3.setCreatedAt(fixedDate);

        // Assert Equals & HashCode
        assertEquals(kitnet1, kitnet2); // Agora vai passar!
        assertEquals(kitnet1.hashCode(), kitnet2.hashCode());
        assertNotEquals(kitnet1, kitnet3);

        // ToString
        String stringResult = kitnet1.toString();
        assertTrue(stringResult.contains("Kitnet"));
        assertTrue(stringResult.contains("id=1"));
        assertTrue(stringResult.contains("name=Kitnet A"));
    }
}