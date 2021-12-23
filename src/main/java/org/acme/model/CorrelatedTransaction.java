package org.acme.model;

import javax.validation.Payload;

public class CorrelatedTransaction {

    public String id;//uui
    public String currentStep;
    public Card payload;
    public String sagaStatus;
    public String stepStatus;
    public String type;
    public String version;

    private CorrelatedTransaction(Builder builder){

    }
    public static Builder newBuilder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public Card getPayload() {
        return payload;
    }

    public String getSagaStatus() {
        return sagaStatus;
    }

    public String getStepStatus() {
        return stepStatus;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "CorrelatedTransaction{" +
                "id='" + id + '\'' +
                ", currentStep='" + currentStep + '\'' +
                ", payload=" + payload +
                ", sagaStatus='" + sagaStatus + '\'' +
                ", stepStatus='" + stepStatus + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

    public static final class Builder {
        public String id;//uui
        public String currentStep;
        public Card payload;
        public String sagaStatus;
        public String stepStatus;
        public String type;
        public String version;

        public Builder() {
        }

        public Builder withId(String val){
            id = val;
            return this;
        }
        public Builder withCurrentStep(String val){
            currentStep = val;
            return this;
        }
        public Builder withStepStatus(String val){
            stepStatus = val;
            return this;
        }

        public Builder withPayload(Card val){
            payload = val;
            return this;
        }
        public Builder withSagaStatus(String val){
            sagaStatus = val;
            return this;
        }
        public Builder withType(String val){
            type = val;
            return this;
        }
        public Builder withVersion(String val){
            version = val;
            return this;
        }

        public CorrelatedTransaction build(){return new CorrelatedTransaction(this);}
    }
}
