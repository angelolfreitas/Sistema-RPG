package com.ieji.rpg.service.monstro;

import com.ieji.rpg.domain.entity.monstro.Monstro;
import com.ieji.rpg.infra.repository.MonstroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
/// Serivce para cachear os mosntros com redis
@Service
@RequiredArgsConstructor
public class MonstroCacheService {

    private final MonstroRepository repository;

    @Cacheable(value = "monstros", key = "'all'")
    public List<Monstro> listarTodosCacheado() {
        return repository.findAll();
    }
}