package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;

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