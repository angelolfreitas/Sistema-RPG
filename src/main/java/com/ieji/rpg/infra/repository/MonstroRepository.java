package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.monstro.Monstro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonstroRepository extends JpaRepository<Monstro, Integer> {
}
