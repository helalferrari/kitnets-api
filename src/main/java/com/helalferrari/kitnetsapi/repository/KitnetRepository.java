package com.helalferrari.kitnetsapi.repository;

import com.helalferrari.kitnetsapi.model.Kitnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Informamos ao Spring: Gerencie a Entidade Kitnet, que tem a PK do tipo Integer
@Repository
public interface KitnetRepository extends JpaRepository<Kitnet, Integer> {

    List<Kitnet> findByDescricaoContainingAndValorBetween(
            String termo,
            Double valorMinimo,
            Double valorMaximo
    );

}