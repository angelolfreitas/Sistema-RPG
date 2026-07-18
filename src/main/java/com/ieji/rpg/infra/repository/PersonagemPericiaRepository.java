package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.PersonagemPericia;
import com.ieji.rpg.domain.entity.PersonagemPericiaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonagemPericiaRepository extends JpaRepository<PersonagemPericia, PersonagemPericiaId> {

    void deleteByPersonagem_IdPersonagem(Integer idPersonagem);
}