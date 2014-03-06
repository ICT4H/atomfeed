package org.ict4h.atomfeed.transaction;

public interface AFTransactionWork<T> {

    enum PropagationDefinition {
        PROPAGATION_REQUIRED, PROPAGATION_REQUIRES_NEW
    }

    T execute();

    PropagationDefinition getTxPropagationDefinition();
}
