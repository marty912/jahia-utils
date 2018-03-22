package com.github.marty912.hibernate.services;

import com.github.marty912.hibernate.model.AbstractIdentifiedEntity;
import com.github.marty912.hibernate.repositories.IRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
public abstract class AbstractCrudService<ENTITY extends AbstractIdentifiedEntity<ID>, ID extends Serializable> extends AbstractService<ENTITY, ID> implements ICrudService<ENTITY, ID> {

    protected AbstractCrudService(IRepository<ENTITY, ID> repository) {
        super(repository);
    }

    @Override
    @Transactional
    public ENTITY create(ENTITY entity) {
        return this.repository.persist(entity);
    }

    @Override
    @Transactional
    public ENTITY update(ENTITY entity) {
        return this.repository.merge(entity);
    }

    @Override
    @Transactional
    public void delete(ENTITY entity) {
        this.repository.delete(entity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        this.repository.deleteById(id);
    }
}
