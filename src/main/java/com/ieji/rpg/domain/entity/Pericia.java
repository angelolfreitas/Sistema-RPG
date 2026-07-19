package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;
/// tabela de perícias disponíveis no sistema.
/// Possui o id da perícia
/// o nome (até 50 caracteres)
/// o atributoBase (atributo do personagem que rege essa perícia, até 20 caracteres)
@Entity
@Table(name = "pericia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pericia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pericia")
    private Integer idPericia;

    @Column(nullable = false, length = 50)
    private String nome;

    @Column(name = "atributo_base", nullable = false, length = 20)
    private String atributoBase;
}