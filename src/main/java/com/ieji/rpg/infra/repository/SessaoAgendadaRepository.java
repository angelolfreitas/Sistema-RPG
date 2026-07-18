package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.SessaoAgendada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessaoAgendadaRepository extends JpaRepository<SessaoAgendada, Integer> {
    List<SessaoAgendada> findByCaso_IdCasoOrderByDataSessaoAsc(Integer idCaso);
}