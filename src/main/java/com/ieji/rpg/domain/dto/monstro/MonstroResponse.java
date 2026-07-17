package com.ieji.rpg.domain.dto.monstro;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.monstro.MaterialMonstro;
import com.ieji.rpg.domain.entity.monstro.Monstro;
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
    private MaterialMonstro material;


    @Override public Integer getId() { return id; }
    public static MonstroResponse constructByEntity(Monstro monstro) {
        return MonstroResponse.builder()
                .id(monstro.getIdMonstro())
                .nome(monstro.getNome())
                .pv(monstro.getPv())
                .pvMaximo(monstro.getPvMaximo())
                .san(monstro.getSan())
                .ataquesEspeciais(monstro.getAtaquesEspeciais())
                .comportamento(monstro.getComportamento())
                .fraquezas(monstro.getFraquezas())
                .imagemUrl(monstro.getImagemUrl())
                .emBatalha(monstro.getEmBatalha())
                .conhecido(true)
                .material(monstro.getMaterial())
                .build();
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
                .material(monstro.getMaterial())
                .build();
    }
}