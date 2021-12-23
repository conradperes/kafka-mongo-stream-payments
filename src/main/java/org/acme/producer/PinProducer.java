package org.acme.producer;

import org.acme.model.Transaction;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class PinProducer {
    public static final String STEP_STATUS_STARTED = "STARTED";
    public static final String CURRENT_STEP_VALIDATION = "VALIDATION_PAYMENT";
    private final Logger logger = Logger.getLogger(PinProducer.class);



    @Inject @Channel("pin-authorization-out")
    Emitter <Transaction> emitter;


    public void sendTransactionToKafka(Transaction transaction) {
        logger.info("Pin Producer of transacion=\t"+transaction);
        emitter.send(transaction);
    }
}