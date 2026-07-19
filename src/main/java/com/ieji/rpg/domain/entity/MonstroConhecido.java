package com.ieji.rpg.domain.entity;

import com.ieji.rpg.domain.entity.monstro.Monstro;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
/// tabela associativa registrando quais monstros cada personagem já conhece.
/// Possui o id do registro
/// o monstro conhecido (relação N:1 com Monstro, lazy)
/// o personagem que conhece (relação N:1 com Personagem, lazy)
/// o conhecidoEm (timestamp de quando o conhecimento foi registrado)
/// restrição de unicidade sobre o par (id_monstro, id_personagem), evitando
/// duplicidade de registro de conhecimento para o mesmo monstro/personagem.
@Entity
@Table(name = "monstro_conhecido", uniqueConstraints = @UniqueConstraint(columnNames = {"id_monstro","id_personagem"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonstroConhecido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_monstro")
    private Monstro monstro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personagem")
    private Personagem personagem;
    private Instant conhecidoEm;
}