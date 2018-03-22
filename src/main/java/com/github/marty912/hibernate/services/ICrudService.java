package com.github.marty912.hibernate.services;

import com.github.marty912.hibernate.model.AbstractIdentifiedEntity;

import java.io.Serializable;

public interface ICrudService<ENTITY extends AbstractIdentifiedEntity<ID>, ID extends Serializable> extends IService<ENTITY, ID> {

    ENTITY create(final ENTITY entity);

    ENTITY update(final ENTITY entity);

    void delete(final ENTITY entity);

    void deleteById(final ID id);
}
