package com.ieji.rpg.domain.entity;

import com.ieji.rpg.domain.Exception.EstoqueInsuficienteException;
import jakarta.persistence.*;
import lombok.*;
/// tabela de itens do sistema (estoque geral, fora do inventário dos personagens).
/// Possui o id do item
/// o nome (até 100 caracteres)
/// a descricao (texto livre)
/// a quantidade disponível em estoque
///
/// removerEstoque(): subtrai uma quantidade do estoque do item.
/// Lança IllegalArgumentException se a quantidade solicitada for menor ou
/// igual a zero, e EstoqueInsuficienteException se o estoque disponível
/// for menor que a quantidade solicitada; caso contrário, efetiva a subtração.
@Entity
@Table(name = "item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item")
    private Integer idItem;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;


    @Column(nullable = false)
    private Integer quantidade;

    public void removerEstoque(int qtd) {
        if (qtd <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero.");
        }
        if (this.quantidade < qtd) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente. Disponível: " + this.quantidade
            );
        }
        this.quantidade -= qtd;
    }
}