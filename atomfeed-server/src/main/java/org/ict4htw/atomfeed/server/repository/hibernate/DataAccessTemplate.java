package org.ict4htw.atomfeed.server.repository.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class DataAccessTemplate extends HibernateTemplate {

    public DataAccessTemplate(SessionFactory sessionFactory) {
        super(sessionFactory);
        setAllowCreate(false);
    }

    public Object getUniqueResult(String namedQueryName, String[] parameterNames, Object[] parameterValues) {
        return DataAccessUtils.uniqueResult(findByNamedQueryAndNamedParam(namedQueryName, parameterNames, parameterValues));
    }

}
