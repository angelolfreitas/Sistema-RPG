package com.ieji.rpg.domain.entity;

import com.ieji.rpg.domain.entity.monstro.Monstro;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
/// tabela de casos de investigação.
/// Possui o id do caso
/// o nomeCaso (até 150 caracteres)
/// o resumo (texto livre)
/// o objetivo (texto livre)
/// a urgencia (até 50 caracteres)
/// o rodadasRestantes
/// o status (ABERTA, EM_ANDAMENTO ou ENCERRADA; default ABERTA)
/// o mestre responsável pelo caso (relação N:1 com Usuario, lazy)
/// o monstroAtual em batalha, usado na fase de combate (relação N:1 com
/// Monstro, opcional)
/// os jogadores participantes (relação N:N com Usuario, via tabela caso_jogador)
/// as perguntas associadas (relação 1:N com Pergunta, cascade total e
/// orphanRemoval, ou seja, apagar o caso apaga suas perguntas)
/// as pistas associadas (relação 1:N com Pista, cascade total e
/// orphanRemoval, ou seja, apagar o caso apaga suas pistas)
///
/// StatusCaso: enum interno com os possíveis estados do caso
/// (ABERTA, EM_ANDAMENTO, ENCERRADA).
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