package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.domain.entity.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AetherysRepository extends JpaRepository<Aetherys, Integer> {
    Optional<Aetherys> findByNome(String nome);
}
