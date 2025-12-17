package com.helalferrari.kitnetsapi.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Intercepta o erro de tamanho máximo excedido
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {

        Map<String, String> response = new HashMap<>();
        response.put("erro", "Arquivo muito grande");
        response.put("mensagem", "O arquivo enviado excede o tamanho máximo permitido pelo sistema.");

        // Retorna HTTP 413 (Payload Too Large) que é o código correto para isso
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
}