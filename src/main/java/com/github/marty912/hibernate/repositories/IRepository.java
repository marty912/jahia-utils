package com.github.marty912.hibernate.repositories;

import com.github.marty912.hibernate.model.AbstractIdentifiedEntity;
import org.hibernate.Criteria;

import java.io.Serializable;
import java.util.List;

public interface IRepository<ENTITY extends AbstractIdentifiedEntity<ID>, ID extends Serializable> {

    Criteria createCriteria();

    long count();

    ENTITY findOne(final ID id);

    List<ENTITY> findAll();

    ENTITY persist(final ENTITY entity);

    ENTITY merge(final ENTITY entity);

    void delete(final ENTITY entity);

    void deleteById(final ID id);
}
