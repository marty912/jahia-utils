package com.github.marty912.hibernate.model;

import java.io.Serializable;

public abstract class AbstractIdentifiedEntity<ID extends Serializable> implements Serializable {

    public abstract ID getId();

    public abstract void setId(ID id);

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + getId() + ']';
    }
}
