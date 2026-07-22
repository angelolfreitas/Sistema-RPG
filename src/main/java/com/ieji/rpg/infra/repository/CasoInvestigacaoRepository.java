package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.domain.entity.Personagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CasoInvestigacaoRepository extends JpaRepository<CasoInvestigacao, Integer> {
    List<CasoInvestigacao> findByMonstroAtual_IdMonstro(Integer idMonstro);
    List<CasoInvestigacao> findByMestre_Id(Integer idMestre);

    @Query("SELECT DISTINCT c FROM CasoInvestigacao c JOIN c.jogadores j WHERE j.id = :usuarioId")
    List<CasoInvestigacao> findByJogadorId(@Param("usuarioId") Integer usuarioId);
}