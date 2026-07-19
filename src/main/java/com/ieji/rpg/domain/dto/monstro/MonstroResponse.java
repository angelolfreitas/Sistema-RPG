package com.ieji.rpg.domain.dto.monstro;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.monstro.MaterialMonstro;
import com.ieji.rpg.domain.entity.monstro.Monstro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
/// DTO de response.
/// possui:
/// Integer: id — id do monstro
/// String: nome
/// Integer: pv
/// Integer: pvMaximo
/// String: san
/// String: ataquesEspeciais
/// String: comportamento
/// String: fraquezas
/// String: imagemUrl
/// Boolean: emBatalha
/// Boolean: conhecido — indica quanto detalhe o usuário atual pode ver
/// MaterialMonstro: material
///
/// constructByEntity(): monta a resposta completa (visão de mestre/conhecido total),
/// mapeando todos os campos da entidade e marcando conhecido = true.
///
/// buildByUser(): monta a resposta oculta para usuário que não conhece o monstro,
/// expondo apenas id, emBatalha, material e conhecido = false (demais campos nulos).
///
/// buildExhibit(): monta a resposta parcial para usuário que já viu o monstro
/// (não é mestre), expondo id, emBatalha, nome, pv, pvMaximo, imagemUrl,
/// material e conhecido = true (sem ataquesEspeciais/comportamento/fraquezas/san).
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
                .material(monstro.getMaterial())
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