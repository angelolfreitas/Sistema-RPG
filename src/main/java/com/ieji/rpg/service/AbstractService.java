package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.BaseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractService <T, ID, DTO extends BaseDTO<ID>, DTI>{

    JpaRepository<T, ID> repository;
    public AbstractService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    abstract DTI construct(DTO object);
    protected abstract void updateData(T entity, DTO object);
    protected abstract DTI convertToResponse(T entity);

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
        Optional<T> findObject = repository.findById(dto.getId());

        if(findObject.isPresent()){
            return Optional.empty();
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
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(entity.getClass(), key);
            field.setAccessible(true);
            ReflectionUtils.setField(field, entity, value);
        });
        this.repository.save(entity);
    }

    public void delete(ID id) {
        Optional<T> findObject = repository.findById(id);
        if(findObject.isEmpty()){
            throw new EntityNotFoundException("Entity not found");
        }
        repository.delete(findObject.get());
    }

}
