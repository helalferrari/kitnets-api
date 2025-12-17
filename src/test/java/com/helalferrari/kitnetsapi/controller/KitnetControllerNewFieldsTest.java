package com.helalferrari.kitnetsapi.controller; // Ajuste o pacote de testes

import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.Landlord;
import com.helalferrari.kitnetsapi.model.Photo;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KitnetController.class) // Foca apenas no KitnetController
class KitnetControllerNewFieldsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KitnetRepository kitnetRepository;

    private Kitnet mockKitnet;

    @BeforeEach
    void setup() {
        // 1. Cria o Landlord
        Landlord landlord = new Landlord(1L, "Bruce Wayne", "bruce@gotham.com", "99 9999-9999");

        // 2. Cria a Kitnet e a vincula ao Landlord
        mockKitnet = new Kitnet();
        mockKitnet.setId(10L);
        mockKitnet.setNome("Mansão Compacta");
        mockKitnet.setValor(5000.00);
        mockKitnet.setLandlord(landlord); // Novo campo Landlord

        // 3. Adiciona Fotos (usando o método auxiliar)
        Photo photo1 = new Photo(20L, "url_sala.jpg", null); // O último 'null' é o Landlord (evitando loop)
        Photo photo2 = new Photo(21L, "url_cozinha.jpg", null);

        // Usamos o método helper para garantir a consistência (seta a kitnet na foto)
        mockKitnet.addPhoto(photo1);
        mockKitnet.addPhoto(photo2);
    }

    @Test
    void shouldReturnKitnetWithLandlordAndPhotos() throws Exception {
        // MOCK: Quando o Controller buscar por ID, retorne a Kitnet completa
        when(kitnetRepository.findById(10L)).thenReturn(Optional.of(mockKitnet));

        // AÇÃO & VERIFICAÇÃO
        mockMvc.perform(get("/api/kitnets/{id}", 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                // 1. Verificar Landlord (Proprietário)
                .andExpect(jsonPath("$.landlord").exists())
                .andExpect(jsonPath("$.landlord.name").value("Bruce Wayne"))
                .andExpect(jsonPath("$.landlord.email").value("bruce@gotham.com"))

                // 2. Verificar Photos (Galeria)
                .andExpect(jsonPath("$.photos").exists())
                .andExpect(jsonPath("$.photos.length()").value(2))
                .andExpect(jsonPath("$.photos[0].url").value("url_sala.jpg"))

                // 3. Verificar Campo Antigo
                .andExpect(jsonPath("$.nome").value("Mansão Compacta"));
    }
}