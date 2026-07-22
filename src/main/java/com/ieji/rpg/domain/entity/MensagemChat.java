package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
/// tabela de mensagens do chat de um caso.
/// Possui o id da mensagem
/// o caso ao qual pertence (relação N:1 com CasoInvestigacao, lazy)
/// o autor da mensagem (relação N:1 com Usuario, lazy; pode ser nulo caso
/// o usuário autor tenha sido excluído do sistema, preservando o histórico)
/// o conteudo (texto livre)
/// o enviadoEm (data/hora de envio)
/// o nomeExibicao (nome já resolvido no momento do envio, ex.:
/// "nomeJogador - username", preservado mesmo que o autor seja desvinculado depois)
@Entity
@Table(name = "mensagem_chat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensagemChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caso", nullable = false)
    private CasoInvestigacao caso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_autor")
    private Usuario autor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(name = "enviado_em", nullable = false)
    private Instant enviadoEm;

    @Column(name = "nome_exibicao", nullable = false)
    private String nomeExibicao;
    
}