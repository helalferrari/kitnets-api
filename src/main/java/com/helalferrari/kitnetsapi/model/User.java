package com.helalferrari.kitnetsapi.model;

import com.helalferrari.kitnetsapi.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    // Getters e Setters normais
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    @Column(unique = true)
    private String email;

    @Setter
    private String password;

    @Setter
    @Getter
    private String phone;

    @Setter
    @Getter
    private String cpf;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDate createdAt = LocalDate.now();

    // Construtores
    public User() {}

    public User(String name, String email, String password, UserRole role, String cpf, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phone = phone;
        this.cpf = cpf;
    }

    // --- MÉTODOS DO SPRING SECURITY (UserDetails) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == UserRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_LANDLORD"), new SimpleGrantedAuthority("ROLE_TENANT"));
        } else if (this.role == UserRole.LANDLORD) {
            return List.of(new SimpleGrantedAuthority("ROLE_LANDLORD"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_TENANT"));
        }
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // O email será o login
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}