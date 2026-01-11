package com.helalferrari.kitnetsapi.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@AllArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "kitnet_id")
    private Kitnet kitnet;

    public Photo(String url) {
        this.url = url;
    }
}