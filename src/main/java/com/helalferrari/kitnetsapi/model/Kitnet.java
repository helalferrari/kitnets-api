package com.helalferrari.kitnetsapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

// Anotações JPA e Lombok
@Entity
@Table(name = "KITNET")
@Data // Lombok: Gera Getters, Setters, toString, equals e hashCode
@NoArgsConstructor // Lombok: Gera um construtor vazio
@AllArgsConstructor // Lombok: Gera um construtor com todos os campos
public class Kitnet {

    // Chave Primária (PK - id)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Colunas de Identificação e Características
    private String nome;
    private Double valor;
    private Double taxa;
    private Integer vagas;
    private String descricao;

    // Colunas de Data (usando o padrão moderno Java 8+)
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_validade")
    private LocalDateTime dataValidade;
}
