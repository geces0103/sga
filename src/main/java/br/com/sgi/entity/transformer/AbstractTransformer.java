package br.com.sgi.entity.transformer;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public abstract class AbstractTransformer<E, D> {

    @Autowired
    private ModelMapper modelMapper;

    private final Class<E> entityClass;
    private final Class<D> dtoClass;

    protected AbstractTransformer(Class<E> entity, Class<D> dto) {
        this.entityClass = entity;
        this.dtoClass = dto;
    }

    public E toEntity(D dto) {
        return modelMapper.map(dto, entityClass);
    }

    public List<E> toEntity(List<D> list) {
        return list.stream().map(this::toEntity).collect(Collectors.toList());
    }

    public Set<E> toEntity(Set<D> set) {
        return set.stream().map(this::toEntity).collect(Collectors.toSet());
    }

    public D toDTO(E entity) {
        return modelMapper.map(entity, dtoClass);
    }

    public List<D> toDTO(List<E> list) {
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }
}