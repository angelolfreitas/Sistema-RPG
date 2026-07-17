package com.ieji.rpg.domain.entity;

import com.ieji.rpg.domain.entity.monstro.Monstro;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

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
    @ManyToOne @JoinColumn(name = "id_monstro") private Monstro monstro;
    @ManyToOne @JoinColumn(name = "id_personagem") private Personagem personagem;
    private Instant conhecidoEm;
}