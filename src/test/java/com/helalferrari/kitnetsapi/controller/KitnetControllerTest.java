package com.helalferrari.kitnetsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helalferrari.kitnetsapi.dto.kitnet.KitnetResponseDTO;
import com.helalferrari.kitnetsapi.infra.security.TokenService;
import com.helalferrari.kitnetsapi.mapper.KitnetMapper;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import com.helalferrari.kitnetsapi.repository.UserRepository;
import com.helalferrari.kitnetsapi.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser; // Importante
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // Importante para PUT/DELETE
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KitnetController.class)
// Simula um usuário logado com o papel LANDLORD para todos os testes
@WithMockUser(username = "dono@email.com", roles = {"LANDLORD"})
class KitnetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- MOCKS DAS DEPENDÊNCIAS DO CONTROLLER ---
    // Como o Controller agora tem construtor cheio de dependências, precisamos mockar todas

    @MockBean
    private KitnetRepository kitnetRepository;

    @MockBean
    private KitnetMapper kitnetMapper; // O Controller usa isso para converter Entity -> DTO

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenService tokenService; // Necessário para o SecurityConfig carregar

    // ---------------------------------------------

    @Test
    void shouldUpdateExistingKitnet() throws Exception {
        Long kitnetId = 1L;
        String updatedName = "Kitnet Atualizada TDD";

        Kitnet inputKitnet = new Kitnet();
        inputKitnet.setNome(updatedName);
        inputKitnet.setValor(1500.00);

        // Objeto retornado pelo banco (mock)
        Kitnet existingKitnet = new Kitnet();
        existingKitnet.setId(kitnetId);
        existingKitnet.setNome("Nome Antigo");

        // Objeto retornado após o save (mock)
        Kitnet savedKitnet = new Kitnet();
        savedKitnet.setId(kitnetId);
        savedKitnet.setNome(updatedName);
        savedKitnet.setValor(1500.00);

        // DTO de resposta (O que o mapper deve devolver)
        KitnetResponseDTO responseDTO = new KitnetResponseDTO(kitnetId, updatedName, 1500.00, 1, 0.0, "", null, null);

        // 1. Simula encontrar a kitnet no banco
        Mockito.when(kitnetRepository.findById(kitnetId)).thenReturn(Optional.of(existingKitnet));

        // 2. Simula salvar a kitnet
        Mockito.when(kitnetRepository.save(any(Kitnet.class))).thenReturn(savedKitnet);

        // 3. Simula a conversão para DTO (IMPORTANTE: Sem isso retorna null no body e falha)
        Mockito.when(kitnetMapper.toResponseDTO(any(Kitnet.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/kitnets/{id}", kitnetId)
                        .with(csrf()) // Necessário para testes de métodos não-GET seguros
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputKitnet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(updatedName));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingKitnet() throws Exception {
        Long nonExistingId = 999L;
        Kitnet updateDetails = new Kitnet();
        updateDetails.setNome("Inexistente");

        Mockito.when(kitnetRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/kitnets/{id}", nonExistingId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                .andExpect(status().isNotFound());

        Mockito.verify(kitnetRepository, Mockito.never()).save(any(Kitnet.class));
    }

    @Test
    void shouldDeleteExistingKitnetAndReturn204() throws Exception {
        Long kitnetId = 1L;

        Mockito.when(kitnetRepository.existsById(kitnetId)).thenReturn(true);
        Mockito.doNothing().when(kitnetRepository).deleteById(kitnetId);

        mockMvc.perform(delete("/api/kitnets/{id}", kitnetId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        Mockito.verify(kitnetRepository, Mockito.times(1)).deleteById(kitnetId);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingKitnet() throws Exception {
        Long nonExistingId = 99999L;

        Mockito.when(kitnetRepository.existsById(nonExistingId)).thenReturn(false); // Ajuste: existsById retorna false

        mockMvc.perform(delete("/api/kitnets/{id}", nonExistingId)
                        .with(csrf()))
                .andExpect(status().isNotFound());

        Mockito.verify(kitnetRepository, Mockito.never()).deleteById(any());
    }

    @Test
    void shouldSearchKitnetsWithAllFilters() throws Exception {
        String cepFiltro = "88050";
        Double minValor = 1300.0;
        Double maxValor = 1500.0;

        Kitnet loftModerno = new Kitnet();
        loftModerno.setId(4L);
        loftModerno.setNome("Loft Moderno");

        // Precisamos simular o DTO também para a lista
        KitnetResponseDTO responseDTO = new KitnetResponseDTO(4L, "Loft Moderno", 1400.0, 1, 0.0, "Desc", null, null);

        Mockito.when(kitnetRepository.findByDescricaoContainingAndValorBetween(cepFiltro, minValor, maxValor))
                .thenReturn(List.of(loftModerno));

        // Simula o Mapper convertendo a lista
        Mockito.when(kitnetMapper.toResponseDTO(loftModerno)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/kitnets/search")
                        .param("cep", cepFiltro)
                        .param("min", String.valueOf(minValor))
                        .param("max", String.valueOf(maxValor))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Loft Moderno"));
    }

    @Test
    void shouldReturnEmptyListForNonMatchingSearch() throws Exception {
        String cepInvalido = "11111-111";
        Double minAbsurdo = 99999.0;
        Double maxAbsurdo = 999999.0;

        Mockito.when(kitnetRepository.findByDescricaoContainingAndValorBetween(cepInvalido, minAbsurdo, maxAbsurdo))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/kitnets/search")
                        .param("cep", cepInvalido)
                        .param("min", String.valueOf(minAbsurdo))
                        .param("max", String.valueOf(maxAbsurdo))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}