package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Pericia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PericiaRepository extends JpaRepository<Pericia, Integer> {
}
