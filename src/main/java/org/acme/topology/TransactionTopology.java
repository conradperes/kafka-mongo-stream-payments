package org.acme.topology;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;
import org.acme.model.CorrelatedTransaction;
import org.acme.model.Transaction;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Produces;
import java.time.Duration;

@ApplicationScoped
public class TransactionTopology {

    public static final int MILLIS_2 = 10000;
    public static final int MILLIS_1000 = 2000;
    private static final Logger logger = Logger.getLogger(TransactionTopology.class);
    static final String TRANSACTION_FINISHED_TOPIC = "transaction-finished";
    static final String CONFIRMATION_AUTHORIZATION_TOPIC = "confirmation-authorization";
    static final String VALIDATION_BIN_TOPIC = "pin-authorization";
    protected static final String SUCEEDEED = "SUCEEDEED";
    protected static final String STARTED = "STARTED";

    @Produces
    public static Topology buildTopology(){
        StreamsBuilder builder = new StreamsBuilder();
        ObjectMapperSerde<Transaction> transactionSerde = new ObjectMapperSerde<>(Transaction.class);
        Duration timeDifference = Duration.ofMillis(MILLIS_2);
        Duration gracePeriod = Duration.ofMillis(MILLIS_1000);
        Serde<String> stringSerde = Serdes.String();
        Serde<Void> voidSerde = Serdes.Void();
        SlidingWindows slidingWindows = SlidingWindows.withTimeDifferenceAndGrace(timeDifference,gracePeriod);
        KStream<Void, Transaction> bankBalanceStream = builder.stream(CONFIRMATION_AUTHORIZATION_TOPIC,
                Consumed.with(Serdes.Void(), transactionSerde))
                .groupByKey()
                .aggregate(Transaction::new,
                        (key, value, aggregate) -> aggregate.update(value),
                        Materialized.with(Serdes.Void(), transactionSerde))
                .toStream();

        KStream<Void, Transaction> validationPinStream = builder.stream(VALIDATION_BIN_TOPIC, Consumed.with(Serdes.Void(),
                transactionSerde))
                .groupByKey()
                .aggregate(Transaction::new,
                        (key, value, aggregate) -> aggregate.update(value),
                        Materialized.with(Serdes.Void(), transactionSerde))
                .filter((unused, transaction) -> transaction.stepStatus.equals(SUCEEDEED) && transaction.sagaStatus.equals(STARTED))
                .toStream();
        //final KTable<String, Transaction> joinTransactionKTable = tableCreation(bankBalanceStream, validationPinStream);
        KStream<Void, CorrelatedTransaction> joinedTransactionsStream = doTransactionJoin(transactionSerde, voidSerde, bankBalanceStream, validationPinStream);
        //Implementar validacao se o table do Validation Pin esta vazio
        //KStream<Void, CorrelatedTransaction> joinTransactions = alternativa3(transactionSerde, voidSerde, bankBalanceStream, validationPinStream);
        //.windowedBy(slidingWindows)
        joinedTransactionsStream.to(TRANSACTION_FINISHED_TOPIC);
        return builder.build();

    }

    @NotNull
    private static KStream<Void, CorrelatedTransaction> alternativa3(ObjectMapperSerde<Transaction> transactionSerde, Serde<Void> voidSerde, KStream<Void, Transaction> bankBalanceStream, KStream<Void, Transaction> validationPinStream) {
        ValueJoiner<Transaction, Transaction, CorrelatedTransaction> transactionBinJoiner = new TransactionBinJoiner();
        JoinWindows tenSeconds = JoinWindows.of(20000);
        KStream<Void, CorrelatedTransaction> joinTransactions = bankBalanceStream.join(validationPinStream,
                transactionBinJoiner,
                tenSeconds,
                Joined.with(voidSerde,
                        transactionSerde,
                        transactionSerde));
        joinTransactions.print(Printed.<Void, CorrelatedTransaction>toSysOut().withLabel("Joined Streams"));
        logger.info(Printed.<Void, CorrelatedTransaction>toSysOut().withLabel("Joined Streams"));
        return joinTransactions;
    }

    private static KTable<String, Transaction> tableCreation(KStream<Void, Transaction> bankBalanceStream, KStream<Void, Transaction> validationPinStream) {
        KTable<String, Transaction> validationPinKTable = validationPinStream.selectKey( (Void, transaction) -> transaction.getId() )
                .groupByKey()
                .aggregate( Transaction::new,
                        (key, value, aggregate) -> value.update(value));

        KTable<String, Transaction> balanceKTable = bankBalanceStream.selectKey( (Void, transaction) -> transaction.getId() )
                .groupByKey()
                .aggregate( Transaction::new,
                        (key, value, aggregate) -> value.update(value));

        //validationPinStream.filter((k, v) -> v.id.length() == 0).process(() -> transactionBinJoiner);
        final KTable<String, Transaction> joinTransactionKTable = validationPinKTable.join(balanceKTable,
                Transaction::getId,
        (transaction, transaction2) -> transaction.update(transaction2));
        return joinTransactionKTable;
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
