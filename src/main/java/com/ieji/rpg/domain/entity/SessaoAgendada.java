package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
/// tabela de sessões agendadas.
/// Possui o id da sessão
/// o caso ao qual pertence (relação N:1 com CasoInvestigacao, lazy)
/// o conteudo (aviso do mestre sobre a sessão, até 2000 caracteres)
/// a dataSessao (data/horário marcado)
/// o criadoEm (timestamp de criação, preenchido automaticamente)
///
/// prePersist(): preenche criadoEm com o instante atual antes da
/// primeira persistência, caso ainda não tenha sido definido.
@Entity
@Table(name = "sessao_agendada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoAgendada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caso_id", nullable = false)
    private CasoInvestigacao caso;

    @Column(nullable = false, length = 2000)
    private String conteudo;

    @Column(name = "data_sessao", nullable = false)
    private LocalDateTime dataSessao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) {
            criadoEm = Instant.now();
        }
    }
}