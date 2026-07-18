package com.ieji.rpg.infra.repository;

import com.ieji.rpg.domain.entity.Item;
import com.ieji.rpg.domain.entity.MensagemChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemChatRepository extends JpaRepository<MensagemChat, Integer> {
    List<MensagemChat> findByCasoIdCasoOrderByEnviadoEmAsc(Integer casoId);
    List<MensagemChat> findByAutor_Id(Integer idAutor);
    void deleteByCasoIdCaso(Integer idCaso);
}
