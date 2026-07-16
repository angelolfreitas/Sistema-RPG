package com.ieji.rpg.domain.entity;

import com.ieji.rpg.domain.Exception.EstoqueInsuficienteException;
import jakarta.persistence.*;
import lombok.*;

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