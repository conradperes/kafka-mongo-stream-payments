package org.acme.topology;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import io.smallrye.reactive.messaging.annotations.Merge;
import org.acme.model.CorrelatedTransaction;
import org.acme.model.Transaction;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

@ApplicationScoped
public class CombinedTransactionsTopology {
    private static final Logger logger = Logger.getLogger(CombinedTransactionsTopology.class);
    static final String TRANSACTION_FINISHED_TOPIC = "transaction-finished";
    static final String CONFIRMATION_AUTHORIZATION_TOPIC = "preview-authorization";
    static final String VALIDATION_BIN_TOPIC = "pin-authorization";
    protected static final String SUCEEDEED = "SUCEEDEED";
    protected static final String STARTED = "STARTED";

//    @Inject
//    @Channel("confirmation-authorization")
//    @Merge
//    static Emitter<Transaction> emitter;

    public static Topology buildTopology(){
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        ObjectMapperSerde<Transaction> transactionSerde = new ObjectMapperSerde<>(Transaction.class);

        Serde<String> stringSerde = Serdes.String();
        Serde<Void> voidSerde = Serdes.Void();
        //emitter.send(transaction);
        final KStream<Void, Transaction> balanceStream = streamsBuilder.stream(CONFIRMATION_AUTHORIZATION_TOPIC);
        final KStream<Void, Transaction> pinStream = streamsBuilder.stream(VALIDATION_BIN_TOPIC);
        KStream<Void, CorrelatedTransaction> joinedTransactionsStream = doTransactionJoin(transactionSerde, voidSerde, balanceStream, pinStream);
        joinedTransactionsStream.to(TRANSACTION_FINISHED_TOPIC);
        return streamsBuilder.build();

    }

    private static KStream<Void, CorrelatedTransaction> doTransactionJoin(ObjectMapperSerde<Transaction> transactionSerde, Serde<Void> voidSerde, KStream<Void, Transaction> bankBalanceStream, KStream<Void, Transaction> validationPinStream) {
        ValueJoiner<Transaction, Transaction, CorrelatedTransaction> transactionBinJoiner = new TransactionBinJoiner();
        JoinWindows tenSeconds = JoinWindows.of(20000);
        KStream<Void, CorrelatedTransaction> joinedTransactionsStream =  bankBalanceStream.outerJoin(
                validationPinStream,
                transactionBinJoiner,
                tenSeconds,
                Joined.with(voidSerde,
                        transactionSerde,
                        transactionSerde))
                .peek((key, value) -> logger.info("Stream-Stream join record key"+ key + "Value "+value));
        joinedTransactionsStream.print(Printed.<Void, CorrelatedTransaction>toSysOut().withLabel("joined Transaction KStream"));
        return joinedTransactionsStream;
    }


}
