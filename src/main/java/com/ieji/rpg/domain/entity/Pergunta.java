package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;
/// tabela de perguntas associadas a um caso de investigação.
/// Possui o id da pergunta
/// o caso ao qual pertence (relação N:1 com CasoInvestigacao, lazy)
/// o textoPergunta (texto livre)
@Entity
@Table(name = "pergunta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pergunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pergunta")
    private Integer idPergunta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caso", nullable = false)
    private CasoInvestigacao caso;

    @Column(name = "texto_pergunta", nullable = false, columnDefinition = "TEXT")
    private String textoPergunta;
}