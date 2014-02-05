package org.ict4h.atomfeed.client.service;


public interface AFTransactionManager {
    public void executeWithTransaction(AFTransactionWork action) throws Exception;
}
