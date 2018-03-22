package com.github.marty912.hibernate.services;

import com.github.marty912.hibernate.model.AbstractIdentifiedEntity;
import com.github.marty912.hibernate.repositories.IRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
public abstract class AbstractService<ENTITY extends AbstractIdentifiedEntity<ID>, ID extends Serializable> implements IService<ENTITY, ID> {

    protected IRepository<ENTITY, ID> repository;

    protected AbstractService(IRepository<ENTITY, ID> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public ENTITY findOne(ID id) {
        return this.repository.findOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ENTITY> findAll() {
        return this.repository.findAll();
    }
}
