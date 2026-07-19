package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
/// tabela de aetherys (equipamentos/artefatos especiais dos personagens).
/// Possui o id do aetherys
/// o nome (até 100 caracteres)
/// a funcao (texto livre)
/// o testeExigido (até 50 caracteres)
/// os personagens que possuem este aetherys (lado inverso da relação N:N,
/// mapeada pelo campo "aetherys" em Personagem)
@Entity
@Table(name = "aetherys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aetherys {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aetherys")
    private Integer idAetherys;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String funcao;

    @Column(name = "teste_exigido", length = 50)
    private String testeExigido;

    @ManyToMany(mappedBy = "aetherys")
    private Set<Personagem> personagens;


}