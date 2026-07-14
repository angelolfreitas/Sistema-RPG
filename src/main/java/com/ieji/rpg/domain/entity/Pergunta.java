package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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