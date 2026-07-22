package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.BaseDTO;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractService<T, ID, DTO extends BaseDTO<ID>, DTI> {

    protected JpaRepository<T, ID> repository;

    public AbstractService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    protected abstract DTI construct(DTO object);
    protected abstract void updateData(T entity, DTO object);
    protected abstract DTI convertToResponse(T entity);

    /// Campos extras que a subclasse quer proteger de PATCH, além dos
    /// automáticos (id, chave composta, relações JPA). Ex.: senha, role.
    protected Set<String> camposProibidosNoPatch() {
        return Collections.emptySet();
    }
    public List<DTI> findAll() {
        List<T> entities = repository.findAll();
        return entities.stream().map(this::convertToResponse).toList();
    }
    public DTI getById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        return convertToResponse(entity);
    }
    public Optional<DTI> create(DTO dto){
        if (dto.getId() != null) {
            Optional<T> findObject = repository.findById(dto.getId());
            if(findObject.isPresent()){
                return Optional.empty();
            }
        }

        return Optional.of(construct(dto));
    }

    @Transactional
    public DTI update(DTO dto){
        T entity = repository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        updateData(entity, dto);

        T updatedEntity = repository.save(entity);

        return convertToResponse(updatedEntity);
    }




    @Transactional
    public void patchEntity(ID id, Map<String, Object> updates) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));

        Set<String> proibidos = camposProibidosNoPatch();

        updates.forEach((key, value) -> {
            if (proibidos.contains(key)) {
                throw new IllegalArgumentException("Campo não editável via PATCH: " + key);
            }

            Field field = ReflectionUtils.findField(entity.getClass(), key);
            if (field == null) {
                throw new IllegalArgumentException("Campo inexistente: " + key);
            }
            if (isProtegidoPorPadrao(field)) {
                throw new IllegalArgumentException("Campo não editável via PATCH: " + key);
            }

            field.setAccessible(true);
            try {
                ReflectionUtils.setField(field, entity, coerceValue(field, value));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Valor inválido para o campo '" + key + "': " + value, e);
            }
        });

        this.repository.save(entity);
    }

    private boolean isProtegidoPorPadrao(Field field) {
        return field.isAnnotationPresent(Id.class)
                || field.isAnnotationPresent(EmbeddedId.class)
                || field.isAnnotationPresent(ManyToOne.class)
                || field.isAnnotationPresent(OneToMany.class)
                || field.isAnnotationPresent(ManyToMany.class)
                || field.isAnnotationPresent(OneToOne.class);
    }

    /// Coerção simples de tipos numéricos: o Jackson desserializa o corpo
    /// JSON como Integer/Double "genéricos", que nem sempre batem com o
    /// tipo exato do campo (ex.: Integer chegando pra um campo Long).
    private Object coerceValue(Field field, Object value) {
        if (value == null) return null;
        Class<?> tipo = field.getType();
        if (tipo.isInstance(value)) return value;

        return switch (value) {
            case Number n when (tipo == Long.class || tipo == long.class) -> n.longValue();
            case Number n when (tipo == Integer.class || tipo == int.class) -> n.intValue();
            case Number n when (tipo == Double.class || tipo == double.class) -> n.doubleValue();
            default -> value;
        };
    }

    public void delete(ID id) {
        Optional<T> findObject = repository.findById(id);
        if (findObject.isEmpty()) {
            throw new EntityNotFoundException("Entity not found");
        }
        repository.delete(findObject.get());
    }
}