package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;
/// tabela associativa entre personagem e perícia (treinamento).
/// Possui o id composto (PersonagemPericiaId: idPersonagem + idPericia)
/// o personagem associado (mapeado via @MapsId em idPersonagem)
/// a pericia associada (mapeado via @MapsId em idPericia)
/// a treinada (flag indicando se o personagem é treinado nessa perícia, default false)
@Entity
@Table(name = "personagem_pericia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonagemPericia {

    @EmbeddedId
    private PersonagemPericiaId id = new PersonagemPericiaId();

    @ManyToOne
    @MapsId("idPersonagem")
    @JoinColumn(name = "id_personagem")
    private Personagem personagem;

    @ManyToOne
    @MapsId("idPericia")
    @JoinColumn(name = "id_pericia")
    private Pericia pericia;

    @Column(nullable = false)
    private Boolean treinada = false;
}