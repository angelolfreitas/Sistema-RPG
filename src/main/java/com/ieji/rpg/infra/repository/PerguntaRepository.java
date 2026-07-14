package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Pergunta;
import com.ieji.rpg.domain.entity.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, Integer> {

}
