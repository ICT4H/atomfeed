package org.ict4h.atomfeed.client.service;

public abstract class AFTransactionWorkWithoutResult implements  AFTransactionWork<Object> {
    @Override
    public Object execute() {
        doInTransaction();
        return null;
    }

    protected abstract void doInTransaction();
}
