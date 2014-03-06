package org.ict4h.atomfeed.transaction;


public interface AFTransactionManager {
    public <T> T executeWithTransaction(AFTransactionWork<T> action) throws RuntimeException;
}
