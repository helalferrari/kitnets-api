package com.helalferrari.kitnetsapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // Gera Getters, Setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Landlord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
}