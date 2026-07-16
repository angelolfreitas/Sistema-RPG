package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, InventarioId> {
    List<Inventario> findByPersonagem_Usuario_Id(Integer usuarioId);
}