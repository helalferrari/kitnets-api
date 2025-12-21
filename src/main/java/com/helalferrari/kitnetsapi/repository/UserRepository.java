package com.helalferrari.kitnetsapi.repository;

import com.helalferrari.kitnetsapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {

    // Método usado pelo Security para achar o usuário no login
    UserDetails findByEmail(String email);
}