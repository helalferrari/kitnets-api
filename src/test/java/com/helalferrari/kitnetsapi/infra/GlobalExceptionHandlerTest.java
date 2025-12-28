package com.helalferrari.kitnetsapi.infra;

import com.helalferrari.kitnetsapi.config.WebConfig;
import com.helalferrari.kitnetsapi.infra.security.SecurityConfigurations;
import com.helalferrari.kitnetsapi.infra.security.TokenService;
import com.helalferrari.kitnetsapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    @DisplayName("Should handle MaxUploadSizeExceededException and return 413 Payload Too Large")
    void handleMaxSizeException() throws Exception {
        mockMvc.perform(get("/throw-max-upload-size"))
                .andExpect(status().is(HttpStatus.PAYLOAD_TOO_LARGE.value()))
                .andExpect(jsonPath("$.erro").value("Arquivo muito grande"))
                .andExpect(jsonPath("$.mensagem").value("O arquivo enviado excede o tamanho máximo permitido pelo sistema."));
    }
}

@RestController
class TestController {
    @GetMapping("/throw-max-upload-size")
    public void throwMaxUploadSizeExceededException() {
        throw new MaxUploadSizeExceededException(1024);
    }
}
