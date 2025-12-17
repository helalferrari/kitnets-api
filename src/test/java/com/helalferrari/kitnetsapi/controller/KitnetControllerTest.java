package com.helalferrari.kitnetsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 1. Diz ao Spring para carregar apenas o contexto web (Controller)
@WebMvcTest(KitnetController.class)
class KitnetControllerTest {

    // 2. Injeta o MockMvc para simular requisições HTTP
    @Autowired
    private MockMvc mockMvc;

    // 3. Simula o Repositório, pois não queremos tocar no banco de dados real
    @MockBean
    private KitnetRepository kitnetRepository;

    // 4. Usado para converter Objetos Java <-> JSON
    @Autowired
    private ObjectMapper objectMapper;

    // O primeiro teste para o PUT virá aqui!

    @Test
    void shouldUpdateExistingKitnet() throws Exception {
        // Arrange (Preparação)
        Integer kitnetId = 1;
        // Objeto de dados simulado para a requisição PUT
        String updatedName = "Kitnet Atualizada TDD";

        // Crie um objeto Kitnet simulado que será enviado no corpo da requisição
        Kitnet updatedDetails = new Kitnet();
        updatedDetails.setNome(updatedName);
        updatedDetails.setValor(1500.00); // Outros campos são necessários para o mapeamento

        // Simulação do Repositório (Mocking)
        // Quando o Controller buscar por ID=1, ele encontrará o objeto.
        Kitnet existingKitnet = new Kitnet(); // A ser buscado
        existingKitnet.setId(kitnetId);

        // Simulação 1: Quando o Controller chamar findById(1), retorna o Optional preenchido
        Mockito.when(kitnetRepository.findById(kitnetId))
                .thenReturn(Optional.of(existingKitnet));

        // Simulação 2: Quando o Controller chamar save(kitnet), retorna o objeto atualizado
        Mockito.when(kitnetRepository.save(Mockito.any(Kitnet.class)))
                .thenReturn(existingKitnet);


        // Act & Assert (Ação e Verificação)
        mockMvc.perform(put("/api/kitnets/{id}", kitnetId) // Requisição PUT para o ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                // Espera-se que o status HTTP seja 200 OK
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingKitnet() throws Exception {
        // Arrange (Preparação)
        Integer nonExistingId = 999;

        // Objeto de dados simulado (não importa o conteúdo, pois a busca falhará)
        Kitnet updateDetails = new Kitnet();
        updateDetails.setNome("Inexistente");

        // Simulação do Repositório (Mocking)
        // Quando o Controller buscar por ID=999, ele retornará Optional Vazio.
        Mockito.when(kitnetRepository.findById(nonExistingId))
                .thenReturn(Optional.empty()); // <-- Simula o 404!

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(put("/api/kitnets/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDetails)))
                // Espera-se que o status HTTP seja 404 NOT FOUND
                .andExpect(status().isNotFound());

        // Garante que o método save() NUNCA foi chamado, pois o item não existe
        Mockito.verify(kitnetRepository, Mockito.never()).save(Mockito.any(Kitnet.class));
    }

    @Test
    void shouldDeleteExistingKitnetAndReturn204() throws Exception {
        // Arrange (Preparação)
        Integer kitnetId = 1;

        // Simulação 1: Quando o Controller chamar existsById(1), retorna TRUE
        Mockito.when(kitnetRepository.existsById(kitnetId))
                .thenReturn(true);

        // Simulação 2: Prepara o método void deleteById para não fazer nada
        Mockito.doNothing().when(kitnetRepository).deleteById(kitnetId);

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(delete("/api/kitnets/{id}", kitnetId)) // Requisição DELETE
                // O padrão REST para exclusão bem-sucedida é 204 No Content
                .andExpect(status().isNoContent());

        // Verificação adicional: Confirma que o método deleteById foi de fato chamado.
        Mockito.verify(kitnetRepository, Mockito.times(1)).deleteById(kitnetId);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingKitnet() throws Exception {
        // Arrange (Preparação)
        Integer nonExistingId = 99999;

        // Simulação: Quando o Controller buscar por ID=999, ele retorna Optional Vazio.
        Mockito.when(kitnetRepository.findById(nonExistingId))
                .thenReturn(Optional.empty());

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(delete("/api/kitnets/{id}", nonExistingId))
                // Espera-se que o status HTTP seja 404 NOT FOUND
                .andExpect(status().isNotFound());

        // Garante que o método deleteById NUNCA foi chamado.
        Mockito.verify(kitnetRepository, Mockito.never()).deleteById(Mockito.anyInt());
    }

    @Test
    void shouldSearchKitnetsWithAllFilters() throws Exception {
        // Arrange (Preparação)
        String cepFiltro = "88050";
        Double minValor = 1300.0;
        Double maxValor = 1500.0;

        // Simulação do resultado esperado do Repositório (apenas o Loft Moderno atende: 1050.00, CEP 88050)
        Kitnet loftModerno = new Kitnet();
        loftModerno.setId(4);
        loftModerno.setNome("Loft Moderno");

        List<Kitnet> expectedList = List.of(loftModerno);

        // Simulação do Repositório (Mocking)
        // Quando o Controller chamar o método do Repository com TODOS os argumentos, retorna nossa lista simulada.
        Mockito.when(kitnetRepository.findByDescricaoContainingAndValorBetween(
                        cepFiltro,
                        minValor,
                        maxValor))
                .thenReturn(expectedList);

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(get("/api/kitnets/search")
                        .param("cep", cepFiltro)   // Parâmetro cep
                        .param("min", String.valueOf(minValor)) // Parâmetro min
                        .param("max", String.valueOf(maxValor)) // Parâmetro max
                        .contentType(MediaType.APPLICATION_JSON))

                // 1. Espera-se que o status HTTP seja 200 OK
                .andExpect(status().isOk())

                // 2. Espera-se que o JSON retornado tenha exatamente 1 item
                .andExpect(jsonPath("$.length()").value(1))

                // 3. Espera-se que o primeiro item seja o esperado
                .andExpect(jsonPath("$[0].nome").value("Loft Moderno"));
    }

    @Test
    void shouldReturnEmptyListForNonMatchingSearch() throws Exception {
        // Arrange (Preparação)
        // Usamos valores que sabemos que não devem existir (ex: CEP muito longo, faixa absurda)
        String cepInvalido = "11111-111";
        Double minAbsurdo = 99999.0;
        Double maxAbsurdo = 999999.0;

        // Simulação do Repositório (Mocking)
        // Quando o Controller chamar o Repositório com ESTES argumentos, ele DEVE retornar uma lista vazia.
        Mockito.when(kitnetRepository.findByDescricaoContainingAndValorBetween(
                        cepInvalido,
                        minAbsurdo,
                        maxAbsurdo))
                .thenReturn(Collections.emptyList()); // Retorna lista vazia

        // Act & Assert (Ação e Verificação)
        mockMvc.perform(get("/api/kitnets/search")
                        .param("cep", cepInvalido)
                        .param("min", String.valueOf(minAbsurdo))
                        .param("max", String.valueOf(maxAbsurdo))
                        .contentType(MediaType.APPLICATION_JSON))

                // 1. Espera-se 200 OK (um resultado vazio é um sucesso REST)
                .andExpect(status().isOk())

                // 2. Espera-se que o JSON retornado tenha EXATAMENTE 0 itens
                .andExpect(jsonPath("$.length()").value(0))

                // 3. Espera-se que o corpo seja uma lista vazia
                .andExpect(jsonPath("$").isEmpty());
    }
}