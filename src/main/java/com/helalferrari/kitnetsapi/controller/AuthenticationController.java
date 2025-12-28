package com.helalferrari.kitnetsapi.controller;

import com.helalferrari.kitnetsapi.dto.auth.AuthenticationDTO;
import com.helalferrari.kitnetsapi.dto.auth.LoginResponseDTO;
import com.helalferrari.kitnetsapi.dto.auth.RegisterDTO;
import com.helalferrari.kitnetsapi.infra.security.TokenService;
import com.helalferrari.kitnetsapi.model.User;
import com.helalferrari.kitnetsapi.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserRepository userRepository,
                                    TokenService tokenService,
                                    PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        // 1. Autentica o usuário
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // 2. Recupera o objeto User completo do principal
        User user = (User) auth.getPrincipal();

        // 3. Gera o token
        var token = tokenService.generateToken(user);

        // 4. Retorna o DTO atualizado com Token + Dados do Usuário
        // Obs: assumindo que user.getRole() retorna um Enum, usamos .toString() ou .getRole() direto se for String
        return ResponseEntity.ok(new LoginResponseDTO(token, user.getName(), user.getRole().toString()));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(this.userRepository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = this.passwordEncoder.encode(data.password());

        // AJUSTE AQUI: Adicionados cpf e phone para bater com o construtor da classe User
        User newUser = new User(
                data.name(),
                data.email(),
                encryptedPassword,
                data.role(),
                data.cpf(),    // Novo campo
                data.phone()   // Novo campo
        );

        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}