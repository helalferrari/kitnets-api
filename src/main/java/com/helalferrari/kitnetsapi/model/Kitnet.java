package com.helalferrari.kitnetsapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KITNET")
@Data // Lombok: Gera Getters, Setters, toString, equals e hashCode
@NoArgsConstructor // Lombok: Gera um construtor vazio
@AllArgsConstructor // Lombok: Gera um construtor com todos os campos
public class Kitnet {

    // Chave Primária (PK - id)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Colunas de Identificação e Características
    private String nome;
    private Double valor;
    private Double taxa;
    private Integer vagas;
    private String descricao;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro = LocalDateTime.now(); // Inicializa com data atual

    @Column(name = "data_validade")
    private LocalDateTime dataValidade;

    // Address Fields
    @Column(name = "cep")
    private String cep;

    @Column(name = "logradouro")
    private String logradouro;

    @Column(name = "complement")
    private String complement;

    @Column(name = "number")
    private String number;

    @Column(name = "neighborhood")
    private String neighborhood;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "ibge")
    private String ibge;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.number == null || this.number.isBlank()) {
            this.number = "S/N";
        }
    }

    // --- ALTERAÇÃO AQUI ---
    // Agora a Kitnet pertence a um User (que tem o papel de LANDLORD)
    @ManyToOne
    @JoinColumn(name = "user_id") // Nome da coluna FK no banco de dados
    private User user;
    // ----------------------

    @OneToMany(mappedBy = "kitnet", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Evita Loop Infinito no Log/Debug
    private List<Photo> photos = new ArrayList<>();

    // Helper Method: Mantém a consistência da lista e do objeto pai
    public void addPhoto(Photo photo) {
        photos.add(photo);
        photo.setKitnet(this);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
        photo.setKitnet(null);
    }
}