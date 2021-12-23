package org.acme.model;

public class ResultTransaction {
    public String id;
    public String currentStep;
    public Card payload;
    public String sagaStatus;
    public String stepStatus;
    public String type;
    public String version;

    public ResultTransaction process(Transaction transaction)
    {
        this.id = transaction.getId();
        this.currentStep = transaction.getCurrentStep();
        this.payload = transaction.getPayload();
        this.sagaStatus = transaction.getSagaStatus();
        this.stepStatus = transaction.getStepStatus();
        this.type = transaction.getType();
        this.version = transaction.getVersion();
        return this;
    }
}
