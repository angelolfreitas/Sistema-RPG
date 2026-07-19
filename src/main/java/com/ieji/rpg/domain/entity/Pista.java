package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;
/// tabela de pistas de um caso de investigação.
/// Possui o id da pista
/// o caso ao qual pertence (relação N:1 com CasoInvestigacao, lazy)
/// a descricao (texto livre)
/// o tipo (categoria da pista, até 50 caracteres)
/// a descoberta (flag indicando se já foi descoberta pelos jogadores, default false)
@Entity
@Table(name = "pista")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pista")
    private Integer idPista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caso", nullable = false)
    private CasoInvestigacao caso;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false)
    private Boolean descoberta = false;
}
