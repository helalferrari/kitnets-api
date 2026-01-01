package com.helalferrari.kitnetsapi.mapper;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetRequestDTO;
import com.helalferrari.kitnetsapi.dto.kitnet.KitnetResponseDTO;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.Photo;
import com.helalferrari.kitnetsapi.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KitnetMapperTest {

    // Instancia a classe diretamente (teste unitário puro)
    private final KitnetMapper mapper = new KitnetMapper();

    @Test
    @DisplayName("Should map Entity to ResponseDTO with all nested objects")
    void shouldMapEntityToResponseDTO() {
        // Arrange
        // 1. Cria o User (Landlord)
        User user = new User();
        user.setId(50L);
        user.setName("Sr. Barriga");
        user.setEmail("cobrador@vila.com");
        user.setPhone("999");

        // 2. Cria a Foto com URL simulando o padrao /uploads/ID/arquivo
        String originalUrl = "/uploads/50/foto-chaves.jpg";
        Photo photo = new Photo(10L, originalUrl, null);
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);

        // 3. Cria a Kitnet completa
        Kitnet kitnet = new Kitnet();
        kitnet.setId(1L);
        kitnet.setNome("Vila do Chaves");
        kitnet.setValor(500.0);
        kitnet.setVagas(2);
        kitnet.setTaxa(10.0);
        kitnet.setDescricao("Boa vizinhanca");
        kitnet.setUser(user);
        kitnet.setPhotos(photos);
        
        // Address fields
        kitnet.setCep("00000-000");
        kitnet.setLogradouro("Rua");
        kitnet.setComplement("Comp");
        kitnet.setNumber("123");
        kitnet.setNeighborhood("Bairro");
        kitnet.setCity("Cidade");
        kitnet.setState("UF");
        kitnet.setIbge("1234567");
        kitnet.setLongitude("1.0");
        kitnet.setLatitude("2.0");
        kitnet.setNonumber(false);

        // Act
        KitnetResponseDTO result = mapper.toResponseDTO(kitnet);

        // Assert
        assertNotNull(result);
        assertEquals(kitnet.getId(), result.getId());
        assertEquals(kitnet.getNome(), result.getNome());
        assertEquals(kitnet.getCep(), result.getCep());
        assertEquals(kitnet.getLogradouro(), result.getLogradouro());
        assertEquals(kitnet.getComplement(), result.getComplement());
        assertEquals(kitnet.getNumber(), result.getNumber());
        assertEquals(kitnet.getNeighborhood(), result.getNeighborhood());
        assertEquals(kitnet.getCity(), result.getCity());
        assertEquals(kitnet.getState(), result.getState());
        assertEquals(kitnet.getIbge(), result.getIbge());
        assertEquals(kitnet.getLongitude(), result.getLongitude());
        assertEquals(kitnet.getLatitude(), result.getLatitude());
        assertEquals(kitnet.getNonumber(), result.getNonumber());

        // Verifica mapeamento do User -> OwnerDTO
        assertNotNull(result.getLandlord());
        assertEquals(user.getId(), result.getLandlord().id());
        assertEquals(user.getName(), result.getLandlord().name());

        // Verifica mapeamento de List<Photo> -> List<PhotoDTO>
        assertNotNull(result.getPhotos());
        assertFalse(result.getPhotos().isEmpty());
        assertEquals(originalUrl, result.getPhotos().get(0).getUrl());
        
        // Verifica se a URL do thumbnail foi gerada corretamente
        assertEquals("/uploads/50/thumbnails/foto-chaves.jpg", result.getPhotos().get(0).getThumbnailUrl());
    }

    @Test
    @DisplayName("Should map Entity to ResponseDTO ignoring null nested objects")
    void shouldMapEntityWithNullAssociations() {
        // Arrange
        Kitnet kitnet = new Kitnet();
        kitnet.setId(2L);
        kitnet.setUser(null);   // Sem dono
        kitnet.setPhotos(null); // Sem fotos

        // Act
        KitnetResponseDTO result = mapper.toResponseDTO(kitnet);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertNull(result.getLandlord()); // Deve continuar nulo
        assertNull(result.getPhotos());   // Deve continuar nulo
    }

    @Test
    @DisplayName("Should return null when input Entity is null")
    void shouldReturnNullFromEntity() {
        assertNull(mapper.toResponseDTO(null));
    }

    @Test
    @DisplayName("Should map RequestDTO to Entity")
    void shouldMapRequestDTOToEntity() {
        // Arrange
        KitnetRequestDTO dto = new KitnetRequestDTO(
                "Kitnet Nova",
                1200.0,
                1,
                50.0,
                "Descricao",
                99L,
                "01001-000",
                "Praça da Sé",
                "lado ímpar",
                "100",
                "Sé",
                "São Paulo",
                "SP",
                "3550308",
                "-49.0629788",
                "-26.9244749",
                false
        );

        // Act
        Kitnet result = mapper.toEntity(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.nome(), result.getNome());
        assertEquals(dto.valor(), result.getValor());
        assertEquals(dto.vagas(), result.getVagas());
        assertEquals(dto.taxa(), result.getTaxa());
        assertEquals(dto.descricao(), result.getDescricao());
        assertEquals(dto.cep(), result.getCep());
        assertEquals(dto.logradouro(), result.getLogradouro());
        assertEquals(dto.complement(), result.getComplement());
        assertEquals(dto.number(), result.getNumber());
        assertEquals(dto.neighborhood(), result.getNeighborhood());
        assertEquals(dto.city(), result.getCity());
        assertEquals(dto.state(), result.getState());
        assertEquals(dto.ibge(), result.getIbge());
        assertEquals(dto.longitude(), result.getLongitude());
        assertEquals(dto.latitude(), result.getLatitude());
        assertEquals(dto.nonumber(), result.getNonumber());
        // Note: O mapper não seta o ID nem o User, isso é feito no Service
    }

    @Test
    @DisplayName("Should return null when input RequestDTO is null")
    void shouldReturnNullFromRequestDTO() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    @DisplayName("Should generate thumbnail URL correctly for different path formats")
    void shouldGenerateThumbnailUrlCorrectly() {
        // Case 1: Root file (should ideally not happen with our storage service but for robustness)
        Photo photo1 = new Photo("foto.jpg");
        assertEquals("foto.jpg", mapper.toResponseDTO(wrapInKitnet(photo1)).getPhotos().get(0).getThumbnailUrl());

        // Case 2: File with slash but no path before it (e.g. /foto.jpg)
        Photo photo2 = new Photo("/foto.jpg");
        assertEquals("/thumbnails/foto.jpg", mapper.toResponseDTO(wrapInKitnet(photo2)).getPhotos().get(0).getThumbnailUrl());

        // Case 3: Standard upload path
        Photo photo3 = new Photo("/uploads/1/foto.jpg");
        assertEquals("/uploads/1/thumbnails/foto.jpg", mapper.toResponseDTO(wrapInKitnet(photo3)).getPhotos().get(0).getThumbnailUrl());
    }

    private Kitnet wrapInKitnet(Photo photo) {
        Kitnet kitnet = new Kitnet();
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        kitnet.setPhotos(photos);
        return kitnet;
    }
}