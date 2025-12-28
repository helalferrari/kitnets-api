package com.helalferrari.kitnetsapi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class PhotoTest {

    @Test
    @DisplayName("Should create Photo with custom constructor")
    void shouldCreatePhotoWithCustomConstructor() {
        // Arrange
        String url = "http://cloudinary.com/img1.jpg";

        // Act
        Photo photo = new Photo(url);

        // Assert
        assertNotNull(photo);
        // Ordem: (Esperado, Atual)
        assertEquals(url, photo.getUrl());
        assertNull(photo.getId());
        assertNull(photo.getKitnet());
    }

    @Test
    @DisplayName("Should create Photo with AllArgsConstructor")
    void shouldCreatePhotoWithAllArgsConstructor() {
        // Arrange
        Long id = 100L;
        String url = "http://s3.aws.com/photo.png";
        // Mockamos o Kitnet para não depender da classe real
        Kitnet kitnetMock = Mockito.mock(Kitnet.class);

        // Act
        Photo photo = new Photo(id, url, kitnetMock);

        // Assert
        assertEquals(id, photo.getId());
        assertEquals(url, photo.getUrl());
        assertEquals(kitnetMock, photo.getKitnet());
    }

    @Test
    @DisplayName("Should test equals and hashCode")
    void shouldTestEqualsAndHashCode() {
        // Arrange
        Photo photo1 = new Photo(1L, "url1", null);
        Photo photo2 = new Photo(1L, "url1", null); // Igual ao 1
        Photo photo3 = new Photo(2L, "url2", null); // Diferente

        // Assert Equals
        assertEquals(photo2, photo1);
        assertNotEquals(photo3, photo1);

        assertNotEquals(null, photo1);
        assertNotEquals(new Object(), photo1);

        // Assert HashCode
        assertEquals(photo2.hashCode(), photo1.hashCode());
        assertNotEquals(photo3.hashCode(), photo1.hashCode());
    }

    @Test
    @DisplayName("Should test toString")
    void shouldTestToString() {
        // Arrange
        Photo photo = new Photo(55L, "http://test.url", null);

        // Act
        String result = photo.toString();

        // Assert
        assertTrue(result.contains("Photo"));
        assertTrue(result.contains("55"));
        assertTrue(result.contains("http://test.url"));
    }

    @Test
    @DisplayName("Should use setters and getters correctly")
    void shouldUseSettersAndGetters() {
        // Arrange
        Photo photo = new Photo();
        Kitnet kitnet = new Kitnet();
        String url = "abc";

        // Act
        photo.setKitnet(kitnet);
        photo.setUrl(url);
        photo.setId(10L);

        // Assert
        assertEquals(kitnet, photo.getKitnet());
        assertEquals(url, photo.getUrl());
        assertEquals(10L, photo.getId());
    }
}