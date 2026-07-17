package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.domain.entity.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonagemRepository extends JpaRepository<Personagem, Integer> {
    Optional<Personagem> findByNomeJogador(String nome);

    Optional<Personagem> findFirstByUsuarioIdOrderByIdPersonagemDesc(Integer usuarioId);
    @Query("SELECT DISTINCT p FROM Personagem p " +
            "LEFT JOIN FETCH p.aetherys " +
            "JOIN FETCH p.usuario " +
            "WHERE p.usuario.id = :usuarioId")
    List<Personagem> findByUsuarioId(@Param("usuarioId") Integer usuarioId);

}