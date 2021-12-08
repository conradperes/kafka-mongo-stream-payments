package org.acme.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.acme.model.Transaction;
import org.acme.service.TransactionService;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
//import org.apache.kafka.streams.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.TimeoutException;


@ApplicationScoped
public class FinalProducer {

    public static final String NOT_AUTHORIZED = "NOT_AUTHORIZED";
    public static final String ABORTED = "ABORTED";
    public static final String COMPENSATE = "COMPENSATE";
    private final Logger logger = Logger.getLogger(FinalProducer.class);
    static final String TRANSACTION_FINISHED_TOPIC = "transaction-finished";
    public static final String VALIDATION_BALANCE = "VALIDATION_BALANCE";
    public static final String VALIDATION_PIN = "VALIDATION_PIN";
    private static final String REQUEST = "REQUEST";
    private static final String CANCEL = "CANCEL";
    protected static final String PAYMENT = "payment";
    protected static final String SUCEEDEED = "SUCEEDEED";
    protected static final String STARTED = "STARTED";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Inject
    TransactionService transactionService;

    @Inject
    @Channel("compensation-authorization-out")
     Emitter<Transaction> emitterCompensation;

    @Inject
    @Channel("transaction-result-out")
    Emitter<Transaction> emmiterResult;




    @SneakyThrows
    public void sendTransactionToKafka(Transaction transaction) {
        logger.info("Final Producer sending last message=" + transaction.toString());
        try {
            if (transaction != null) {
                if (transaction.stepStatus.equals(SUCEEDEED) &&
                        transaction.sagaStatus.equals(STARTED)) {
                    //emitter.send(transaction);
                    transactionService.insertTransaction(transaction);
                    //CombinedTransactionsTopology.buildTopology();
                    emmiterResult.send(transaction);
                    logger.info("Transaction Succeeded=" + transaction.id);
                }if (transaction.stepStatus.equals(ABORTED)) {
                    transaction.sagaStatus = COMPENSATE;
                    emitterCompensation.send(transaction);
                    logger.info("Transaction " + COMPENSATE + " ID=" + transaction.id);
                }if( !transaction.sagaStatus.equals(STARTED)) {
                    transaction.sagaStatus = NOT_AUTHORIZED;
                    emmiterResult.send(transaction);
                    logger.info("Transaction " + NOT_AUTHORIZED + " ID=" + transaction.id);
                }
            } else {
                //emmiterResult.send(transaction);
                logger.info("Empty Record!##### Not sent on the next topic!");
            }
        }catch (TimeoutException e){
            transaction.sagaStatus = "COMPENSATE";
            emitterCompensation.send(transaction);
        }catch (Exception e){
            transaction.stepStatus = "ERROR";
            transaction.sagaStatus = "ERROR";
            emmiterResult.send(transaction);
            e.printStackTrace();
            logger.info("Exception occurred="+e.getMessage());
         }
    }

}