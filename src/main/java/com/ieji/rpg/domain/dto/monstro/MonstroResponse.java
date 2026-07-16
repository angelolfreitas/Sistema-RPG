package com.ieji.rpg.domain.dto.monstro;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Monstro;
import com.ieji.rpg.domain.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class MonstroResponse implements BaseDTO<Integer> {
    private Integer id;
    private String nome;
    private Integer pv;
    private Integer pvMaximo;
    private String san;

    private String ataquesEspeciais;
    private String comportamento;
    private String fraquezas;

    private String imagemUrl;
    private Boolean emBatalha;
    private Boolean conhecido;


    @Override public Integer getId() { return id; }
    public static MonstroResponse constructByEntity(Monstro monstro) {
        return new MonstroResponse(monstro.getIdMonstro(), monstro.getNome(), monstro.getPv(),
                monstro.getPvMaximo(), monstro.getSan(), monstro.getAtaquesEspeciais(),
                monstro.getComportamento(), monstro.getFraquezas(), monstro.getImagemUrl(),
                monstro.getEmBatalha(), true);
    }

    public static MonstroResponse buildByUser(Monstro monstro) {
        return MonstroResponse.builder()
                .id(monstro.getIdMonstro())
                .emBatalha(monstro.getEmBatalha())
                .conhecido(false)
                .build();
    }
    public static MonstroResponse buildExhibit(Monstro monstro) {
        return MonstroResponse.builder()
                .id(monstro.getIdMonstro())
                .emBatalha(monstro.getEmBatalha())
                .nome(monstro.getNome())
                .pv(monstro.getPv())
                .pvMaximo(monstro.getPvMaximo())
                .imagemUrl(monstro.getImagemUrl())
                .conhecido(true)
                .build();
    }
}