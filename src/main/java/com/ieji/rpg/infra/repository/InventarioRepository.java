package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, InventarioId> {

    @Query("SELECT i FROM Inventario i " +
            "JOIN FETCH i.item " +
            "JOIN FETCH i.personagem p " +
            "JOIN FETCH p.usuario " +
            "WHERE p.usuario.id = :usuarioId")
    List<Inventario> findByPersonagem_Usuario_Id(@Param("usuarioId") Integer usuarioId);
}