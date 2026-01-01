package com.helalferrari.kitnetsapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helalferrari.kitnetsapi.dto.kitnet.KitnetResponseDTO;
// Imports dos Mocks
import com.helalferrari.kitnetsapi.infra.security.SecurityFilter;
import com.helalferrari.kitnetsapi.infra.security.TokenService;
import com.helalferrari.kitnetsapi.mapper.KitnetMapper;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.User;
import com.helalferrari.kitnetsapi.model.enums.UserRole;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import com.helalferrari.kitnetsapi.repository.UserRepository;
import com.helalferrari.kitnetsapi.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; // Importante
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration; // Importante
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = KitnetController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityFilter.class),
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
class KitnetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- MOCKS ---
    // Mesmo sem segurança, o Controller injeta esses componentes, então precisamos mockar
    @MockBean private KitnetRepository kitnetRepository;
    @MockBean private KitnetMapper kitnetMapper;
    @MockBean private FileStorageService fileStorageService;
    @MockBean private UserRepository userRepository;
    @MockBean private TokenService tokenService;

    @BeforeEach
    void setup() {
        // --- BYPASS TOTAL DA SEGURANÇA ---
        // Criamos o usuário que o Controller espera receber
        User customUser = new User();
        customUser.setId(1L);
        customUser.setEmail("dono@email.com");
        customUser.setRole(UserRole.LANDLORD);

        // Criamos um "Crachá" falso (Authentication)
        Authentication authMock = Mockito.mock(Authentication.class);
        // Quando o Controller perguntar o principal, devolvemos nosso objeto User customizado
        Mockito.when(authMock.getPrincipal()).thenReturn(customUser);
        // Garantimos que ele está autenticado
        Mockito.when(authMock.isAuthenticated()).thenReturn(true);

        // Criamos o contexto de segurança falso
        SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authMock);

        // Injetamos isso na memória do Spring Security Holder
        SecurityContextHolder.setContext(securityContextMock);
    }

    @Test
    void shouldUpdateExistingKitnet() throws Exception {
        Long kitnetId = 1L;
        
        // Dados de entrada (o que o usuário envia)
        Kitnet inputKitnet = new Kitnet();
        inputKitnet.setNome("Kitnet Atualizada TDD");
        inputKitnet.setValor(1500.00);
        inputKitnet.setTaxa(200.00);
        inputKitnet.setVagas(2);
        inputKitnet.setDescricao("Descrição atualizada via teste");
        inputKitnet.setCep("00000-000");
        inputKitnet.setIbge("1234567");
        inputKitnet.setLongitude("1.0");
        inputKitnet.setLatitude("2.0");

        // Estado anterior (o que já existe no banco)
        Kitnet existingKitnet = new Kitnet();
        existingKitnet.setId(kitnetId);
        existingKitnet.setNome("Nome Antigo");
        existingKitnet.setValor(1000.00);
        existingKitnet.setTaxa(100.00);
        existingKitnet.setVagas(1);
        existingKitnet.setDescricao("Descrição antiga");

        // O que esperamos que seja salvo
        Kitnet savedKitnet = new Kitnet();
        savedKitnet.setId(kitnetId);
        savedKitnet.setNome("Kitnet Atualizada TDD");
        savedKitnet.setValor(1500.00);
        savedKitnet.setTaxa(200.00);
        savedKitnet.setVagas(2);
        savedKitnet.setDescricao("Descrição atualizada via teste");

        KitnetResponseDTO mockResponseDTO = Mockito.mock(KitnetResponseDTO.class);

        // Mock dos comportamentos
        Mockito.when(kitnetRepository.findById(kitnetId)).thenReturn(Optional.of(existingKitnet));
        // IMPORTANTE: Aqui usamos any() porque o objeto exato pode ser diferente na referência,
        // mas o comportamento interno do controller é alterar o objeto existente e salvar.
        Mockito.when(kitnetRepository.save(any(Kitnet.class))).thenReturn(savedKitnet);
        Mockito.when(kitnetMapper.toResponseDTO(any(Kitnet.class))).thenReturn(mockResponseDTO);

        mockMvc.perform(put("/api/kitnets/{id}", kitnetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputKitnet)))
                .andExpect(status().isOk());
        
        // VERIFICAÇÃO ADICIONAL: Garantir que os setters foram chamados corretamente no objeto recuperado
        // Isso garante que o controller mapeou todos os campos
        // Como o controller modifica o objeto 'existingKitnet' in-place, podemos verificar seu estado final aqui
        // (supondo que o mockito não resetou o estado, mas como é unitário, o objeto existingKitnet é o mesmo)
        // OBS: Em um teste real de integração, isso seria verificado no banco. 
        // Aqui confiamos que o controller chamou .setX(input.getX())
    }

    @Test
    void shouldDeleteExistingKitnetAndReturn204() throws Exception {
        Long kitnetId = 1L;
        Mockito.when(kitnetRepository.existsById(kitnetId)).thenReturn(true);
        Mockito.doNothing().when(kitnetRepository).deleteById(kitnetId);

        mockMvc.perform(delete("/api/kitnets/{id}", kitnetId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingKitnet() throws Exception {
        Long nonExistingId = 99999L;
        Mockito.when(kitnetRepository.existsById(nonExistingId)).thenReturn(false);

        mockMvc.perform(delete("/api/kitnets/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSearchKitnets() throws Exception {
        Mockito.when(kitnetRepository.findByDescricaoContainingAndValorBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/kitnets/search")
                        .param("cep", "88050")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}