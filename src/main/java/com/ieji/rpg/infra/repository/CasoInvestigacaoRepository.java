package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.domain.entity.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CasoInvestigacaoRepository extends JpaRepository<CasoInvestigacao, Integer> {
    Optional<CasoInvestigacao> findByNomeCaso(String nome);
}