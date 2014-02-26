package org.ict4h.atomfeed.client.service;


public interface AFTransactionManager {
    public <T> T executeWithTransaction(AFTransactionWork<T> action) throws Exception;
}
