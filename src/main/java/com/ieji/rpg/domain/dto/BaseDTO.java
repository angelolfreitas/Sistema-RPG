package com.ieji.rpg.domain.dto;
/// Abstracao para services e controllers abstratos (mal sucedida, pois pressupoe uma injeção de id mão coerente)
public interface BaseDTO <T> {
    T getId();
}
