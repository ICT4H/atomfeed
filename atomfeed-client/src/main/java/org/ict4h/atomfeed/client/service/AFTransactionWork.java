package org.ict4h.atomfeed.client.service;

public interface AFTransactionWork<T> {

    enum PropagationDefinition {
        PROPAGATION_REQUIRED, PROPAGATION_REQUIRES_NEW
    }

    T execute();

    PropagationDefinition getTxPropagationDefinition();
}
