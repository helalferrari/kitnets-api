package com.helalferrari.kitnetsapi.infra;

import com.helalferrari.kitnetsapi.config.WebConfig;
import com.helalferrari.kitnetsapi.infra.security.SecurityConfigurations;
import com.helalferrari.kitnetsapi.infra.security.TokenService;
import com.helalferrari.kitnetsapi.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TestController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {WebConfig.class, SecurityConfigurations.class}
        )
)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Should instantiate GlobalExceptionHandler")
    void shouldInstantiate() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        assertNotNull(handler);
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle MaxUploadSizeExceededException and return 413 Payload Too Large")
    void handleMaxSizeException() throws Exception {
        mockMvc.perform(get("/throw-max-upload-size"))
                .andExpect(status().is(HttpStatus.PAYLOAD_TOO_LARGE.value()))
                .andExpect(jsonPath("$.erro").value("Arquivo muito grande"))
                .andExpect(jsonPath("$.mensagem").value("O arquivo enviado excede o tamanho máximo permitido pelo sistema."));
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle ResponseStatusException and return correct status code")
    void handleResponseStatusException() throws Exception {
        mockMvc.perform(get("/throw-response-status-exception"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Erro na requisição"))
                .andExpect(jsonPath("$.mensagem").value("Not Found Reason"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle generic Exception and return 500 Internal Server Error")
    void handleGenericException() throws Exception {
        mockMvc.perform(get("/throw-generic-exception"))
                .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.erro").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.mensagem").value("Erro Genérico"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should handle MethodArgumentNotValidException and return 400 Bad Request")
    void handleValidationException() throws Exception {
        // Envia um JSON vazio para que o @NotNull dispare o erro
        mockMvc.perform(post("/validate-body")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campoObrigatorio").exists());
    }
}

@RestController
class TestController {
    
    @GetMapping("/throw-max-upload-size")
    public void throwMaxUploadSizeExceededException() {
        throw new MaxUploadSizeExceededException(1024);
    }

    @GetMapping("/throw-response-status-exception")
    public void throwResponseStatusException() {
        throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found Reason");
    }

    @GetMapping("/throw-generic-exception")
    public void throwGenericException() throws Exception {
        throw new Exception("Erro Genérico");
    }

    @PostMapping("/validate-body")
    public void validateBody(@RequestBody @Valid TestDTO dto) {
        // Nada a fazer, a validação ocorre antes
    }
}

class TestDTO {
    @NotNull(message = "Campo obrigatório")
    private String campoObrigatorio;

    public String getCampoObrigatorio() {
        return campoObrigatorio;
    }

    public void setCampoObrigatorio(String campoObrigatorio) {
        this.campoObrigatorio = campoObrigatorio;
    }
}