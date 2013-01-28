/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.ict4htw.atomfeed.demo.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.ict4htw.atomfeed.demo.db.AtomFeedDAO;
import org.openmrs.OpenmrsObject;

/**
 * It is a default implementation of  {@link AtomFeedDAO}.
 */
public class HibernateAtomFeedDAO implements AtomFeedDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

	@Override
	public OpenmrsObject getObjectByUuid(String classname, String uuid) {
		return (OpenmrsObject)sessionFactory.getCurrentSession().createCriteria(classname).add(Expression.eq("uuid", uuid)).uniqueResult();
	}
}