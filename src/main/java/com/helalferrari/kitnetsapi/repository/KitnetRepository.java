package com.helalferrari.kitnetsapi.repository;

import com.helalferrari.kitnetsapi.model.Kitnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Informamos ao Spring: Gerencie a Entidade Kitnet, que tem a PK do tipo Integer
@Repository
public interface KitnetRepository extends JpaRepository<Kitnet, Long> {

    List<Kitnet> findByDescriptionContainingAndValueBetween(
            String term,
            Double minValue,
            Double maxValue
    );

    List<Kitnet> findByUser(com.helalferrari.kitnetsapi.model.User user);

}