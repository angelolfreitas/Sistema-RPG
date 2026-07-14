package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "caso_investigacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CasoInvestigacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caso")
    private Integer idCaso;

    @Column(name = "nome_caso", nullable = false, length = 150)
    private String nomeCaso;

    @Column(columnDefinition = "TEXT")
    private String resumo;

    @Column(columnDefinition = "TEXT")
    private String objetivo;

    @Column(length = 50)
    private String urgencia;

    @Column(name = "rodadas_restantes")
    private Integer rodadasRestantes;

    @OneToMany(mappedBy = "caso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pergunta> perguntas;

    @OneToMany(mappedBy = "caso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pista> pistas;
}