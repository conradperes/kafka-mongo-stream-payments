package org.acme.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Transaction {



    public static final String TRANSACTION_ALREADY_EXISTS = "TRANSACATION_ALREADY_EXISTS";

    public static final String TRANSACTION_UPDATED = "TRANSACTION_UPDATED";
    public static final String TRANSACTION_RESTORED = "TRANSACTION_RESTORED";

    public String id;//uui
    public String currentStep;
    public Card payload;
    public String sagaStatus;
    public String stepStatus;
    public String type;
    public String version;



    public Transaction() {
    }
    @JsonCreator
    public Transaction(
            @JsonProperty("id")String id,
            @JsonProperty("")String currentStep,
            @JsonProperty("payload")Card payload,
            @JsonProperty("sagaStatus")String sagaStatus,
            @JsonProperty("stepStatus")String stepStatus,
            @JsonProperty("type")String type,
            @JsonProperty("version")String version) {
        this.id = id;
        this.currentStep = currentStep;
        this.payload = payload;
        this.sagaStatus = sagaStatus;
        this.stepStatus = stepStatus;
        this.type = type;
        this.version = version;
    }


    public Transaction update(Transaction transaction){
        if(transaction.id.equals(this.id))
            return this;
        else
            return null;
    }

}