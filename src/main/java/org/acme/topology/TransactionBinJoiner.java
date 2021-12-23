package org.acme.topology;

import org.acme.model.Bin;
import org.acme.model.Card;
import org.acme.model.CorrelatedTransaction;
import org.acme.model.Transaction;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class TransactionBinJoiner  implements ValueJoiner<Transaction, Transaction, CorrelatedTransaction> {
    @Override
    public CorrelatedTransaction apply(Transaction transaction, Transaction otherTransaction) {
        CorrelatedTransaction.Builder builder = CorrelatedTransaction.newBuilder();
        String id = transaction != null ? transaction.getId() : null;
        String currentStep = transaction != null ? transaction.getCurrentStep() : null;
        Card payload = transaction != null ? transaction.getPayload() : null;
        String sagaStatus = transaction != null ? transaction.getSagaStatus() : null;
        String stepStatus = transaction != null ? transaction.getStepStatus() : null;
        String type = transaction != null ? transaction.getType() : null;
        String version = transaction != null ? transaction.getVersion() : null;

        String otherTramsactionId = otherTransaction != null ? otherTransaction.getId() : null;
        String otherTramsactionCurrentStep = otherTransaction != null ? otherTransaction.getCurrentStep() : null;
        Card otherTramsactionPayload = otherTransaction != null ? otherTransaction.getPayload() : null;
        String otherTramsactionSagaStatus = otherTransaction != null ? otherTransaction.getSagaStatus() : null;
        String otherTramsactionStepStatus = otherTransaction != null ? otherTransaction.getStepStatus() : null;
        String otherTramsactionType = otherTransaction != null ? otherTransaction.getType() : null;
        String otherTramsactionVersion = otherTransaction != null ? otherTransaction.getVersion() : null;


        builder.withId(id.equalsIgnoreCase(otherTramsactionId) ? id : otherTramsactionId )
                .withPayload(payload)
                .withCurrentStep(currentStep)
                .withType(type)
                .withSagaStatus(sagaStatus)
                .withVersion(version)
                .withStepStatus(stepStatus);
        return builder.build();
    }



}
