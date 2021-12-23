package org.acme.topology;

import org.acme.model.JsonSerde;
import org.acme.model.Transaction;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.stream.Stream;


public class CreditTransactionTopology {

    static final String TRANSACTION_FINISHED_TOPIC = "transaction-finished";
    static final String CONFIRMATION_AUTHORIZATION_TOPIC = "confirmation-authorization";
    public static final String SUCEEDEED = "SUCEEDEED";
    public static final String BANK_TRANSACTIONS = "bank-transactions";
    public static final String BANK_BALANCES = "bank-balances";
    public static final String REJECTED_TRANSACTIONS = "rejected-transactions";


    public static Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<Void, Transaction> input = builder.stream(CONFIRMATION_AUTHORIZATION_TOPIC);
        final KTable<Integer, Transaction> sumOfOddNumbers = input
                .filter((k, v) -> v.sagaStatus.equals(SUCEEDEED) && v.stepStatus.equals(SUCEEDEED))
                .selectKey((k, v) -> 1)
                .groupByKey()
                .reduce((v1, v2) -> v1.update(v2));
        sumOfOddNumbers.toStream().to(TRANSACTION_FINISHED_TOPIC);
        return builder.build();
    }
}
