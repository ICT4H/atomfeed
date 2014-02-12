package org.ict4h.atomfeed.client.service;

public interface AFTransactionWork {

    enum PropagationDefinition {
        PROPAGATION_REQUIRED, PROPAGATION_REQUIRES_NEW
    }

    void execute();

    PropagationDefinition getTxPropagationDefinition();
}
