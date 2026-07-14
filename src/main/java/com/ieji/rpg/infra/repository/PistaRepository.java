package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Integer> {

}
