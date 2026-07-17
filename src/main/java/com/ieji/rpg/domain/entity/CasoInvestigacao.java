package com.ieji.rpg.domain.entity;

import com.ieji.rpg.domain.entity.monstro.Monstro;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // --- NOVOS CAMPOS PARA SESSÃO ---

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCaso status = StatusCaso.ABERTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mestre", nullable = false)
    private Usuario mestre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_monstro_atual")
    private Monstro monstroAtual; // Para a Fase 2 (Batalha)

    @ManyToMany
    @JoinTable(
            name = "caso_jogador",
            joinColumns = @JoinColumn(name = "id_caso"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private Set<Usuario> jogadores = new HashSet<>();

    // --- RELACIONAMENTOS ORIGINAIS ---

    @OneToMany(mappedBy = "caso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pergunta> perguntas;

    @OneToMany(mappedBy = "caso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pista> pistas;

    public enum StatusCaso {
        ABERTA, EM_ANDAMENTO, ENCERRADA
    }
}