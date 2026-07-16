package com.ieji.rpg.infra.repository;


import com.ieji.rpg.domain.entity.MonstroConhecido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonstroConhecidoRepository extends JpaRepository<MonstroConhecido, Long> {
    boolean existsByMonstro_IdMonstroAndPersonagem_Usuario_Id(Integer idMonstro, Integer usuarioId);
    List<MonstroConhecido> findByPersonagem_Usuario_Id(Integer usuarioId);
}