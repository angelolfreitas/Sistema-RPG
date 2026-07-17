package com.ieji.rpg.domain.entity.monstro;

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

    @Column(name = "pv_maximo", nullable = false)
    private Integer pvMaximo;

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

    @Column(name = "imagem_url")
    private String imagemUrl;

    @Column(name = "em_batalha", nullable = false)
    @Builder.Default
    private Boolean emBatalha = false;

    @Column(name = "conhecido", nullable = false)
    @Builder.Default
    private Boolean conhecido = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "material", nullable = false)
    @Builder.Default
    private MaterialMonstro material = MaterialMonstro.CARNE;
}
