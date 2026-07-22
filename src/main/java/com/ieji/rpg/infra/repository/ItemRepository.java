package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.domain.entity.Item;
import com.ieji.rpg.domain.entity.Personagem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.idItem = :id")
    Optional<Item> findByIdWithLock(@Param("id") Integer id);
}
