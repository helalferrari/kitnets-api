package com.helalferrari.kitnetsapi.model;

import com.helalferrari.kitnetsapi.model.enums.Amenity;
import com.helalferrari.kitnetsapi.model.enums.BathroomType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "KITNET")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kitnet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(name = "\"value\"")
    private Double value;
    
    private Double fee;
    private Integer parkingSpaces;
    private String description;

    private Double area;
    
    private Boolean furnished = false;
    
    private Boolean petsAllowed = false;

    @Enumerated(EnumType.STRING)
    private BathroomType bathroomType;

    @ElementCollection(targetClass = Amenity.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "kitnet_amenities", joinColumns = @JoinColumn(name = "kitnet_id"))
    @Column(name = "amenity")
    private Set<Amenity> amenities = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "cep")
    private String cep;

    @Column(name = "street")
    private String street;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "kitnet", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Photo> photos = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (this.number == null || this.number.isBlank()) {
            this.number = "S/N";
        }
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
        photo.setKitnet(this);
    }

    public void removePhoto(Photo photo) {
        photos.remove(photo);
        photo.setKitnet(null);
    }
}