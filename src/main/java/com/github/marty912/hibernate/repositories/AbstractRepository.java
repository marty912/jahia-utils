package com.github.marty912.hibernate.repositories;

import com.github.marty912.hibernate.model.AbstractIdentifiedEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public abstract class AbstractRepository<ENTITY extends AbstractIdentifiedEntity<ID>, ID extends Serializable> implements IRepository<ENTITY, ID> {
    protected SessionFactory sessionFactory;

    protected final Class<ENTITY> entityClass;

    public AbstractRepository() {
        this.entityClass = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    public Criteria createCriteria() {
        return this.getCurrentSession().createCriteria(entityClass);
    }

    @Override
    public long count() {
        return (long) this.createCriteria()
                .setProjection(Projections.rowCount())
                .uniqueResult();
    }

    @Override
    public ENTITY findOne(ID id) {
        return (ENTITY) this.getCurrentSession().get(entityClass, id);
    }

    @Override
    public List<ENTITY> findAll() {
        return this.createCriteria().list();
    }

    @Override
    public ENTITY persist(ENTITY entity) {
        this.getCurrentSession().persist(entity);
        return entity;
    }

    @Override
    public ENTITY merge(final ENTITY entity) {
        return (ENTITY) this.getCurrentSession().merge(entity);
    }

    @Override
    public void delete(ENTITY entity) {
        this.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(ID entityId) {
        this.delete(this.findOne(entityId));
    }

    protected Session getCurrentSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Autowired
    public void setHibernateTransactionManager(@Qualifier("transactionManager") HibernateTransactionManager hibernateTransactionManager) {
        this.sessionFactory = hibernateTransactionManager.getSessionFactory();
    }
}
