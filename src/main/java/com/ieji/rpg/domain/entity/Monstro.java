package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "monstro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Monstro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_monstro")
    private Integer idMonstro;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer pv;

    @Column(length = 50)
    private String san;

    @Column(name = "ataques_especiais", columnDefinition = "TEXT")
    private String ataquesEspeciais;

    @Column(columnDefinition = "TEXT")
    private String comportamento;

    @Column(columnDefinition = "TEXT")
    private String fraquezas;
}
