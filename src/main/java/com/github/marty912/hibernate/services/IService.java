package com.github.marty912.hibernate.services;

import com.github.marty912.hibernate.model.AbstractIdentifiedEntity;

import java.io.Serializable;
import java.util.List;

public interface IService<ENTITY extends AbstractIdentifiedEntity<ID>, ID extends Serializable> {

    ENTITY findOne(final ID id);

    List<ENTITY> findAll();
}
