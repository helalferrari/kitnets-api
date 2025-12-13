package com.helalferrari.kitnetsapi.repository;

import com.helalferrari.kitnetsapi.model.Kitnet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Informamos ao Spring: Gerencie a Entidade Kitnet, que tem a PK do tipo Integer
@Repository
public interface KitnetRepository extends JpaRepository<Kitnet, Integer> {

    // O Spring Data JPA cria a implementação em tempo de execução.
    // Não precisamos adicionar métodos aqui para operações básicas.

}