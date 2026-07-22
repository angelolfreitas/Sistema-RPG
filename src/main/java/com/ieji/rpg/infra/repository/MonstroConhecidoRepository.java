package com.ieji.rpg.infra.repository;


import com.ieji.rpg.domain.entity.MonstroConhecido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MonstroConhecidoRepository extends JpaRepository<MonstroConhecido, Long> {
    @Deprecated
    @Query("SELECT mc FROM MonstroConhecido mc " +
            "JOIN FETCH mc.monstro " +
            "JOIN FETCH mc.personagem p " +
            "JOIN FETCH p.usuario " +
            "WHERE p.usuario.id = :usuarioId")
    List<MonstroConhecido> findByPersonagem_Usuario_Id(@Param("usuarioId") Integer usuarioId);

    boolean existsByMonstro_IdMonstroAndPersonagem_Usuario_Id(Integer idMonstro, Integer usuarioId);
    @Deprecated
    boolean existsByMonstro_IdMonstroAndPersonagem_IdPersonagem(Integer idMonstro, Integer idPersonagem);
    void deleteByMonstro_IdMonstro(Integer id);
    void deleteByPersonagem_IdPersonagem(Integer idPersonagem);
}