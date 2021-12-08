package org.acme.service;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.acme.consumer.TransactionConsumer;
import org.acme.model.Card;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.acme.model.Transaction;
import org.bson.Document;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import java.util.Date;
import java.util.concurrent.TimeoutException;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class TransactionService {

    public static final int TIME_OUT_LIMIT = 5000;
    @Inject
    MongoClient mongoClient;

    private final Logger logger = Logger.getLogger(TransactionService.class);

    private MongoCollection<Document> getCollection(){
        return mongoClient.getDatabase("transaction").getCollection("transaction");
    }

    public String insertTransaction(Transaction transaction) throws Exception {
        Document document = getCollection().find(eq("id", transaction.id)).first();
        if (document != null) {
            //if (document.getObjectId("id") .equals( transaction.getId())){
                final Date dbTimeStamp = document.getDate("timeStamp");
                final Date actualDate = new Date();
                long diference = actualDate.getTime() - dbTimeStamp.getTime();
                if(diference > TIME_OUT_LIMIT)
                    throw new TimeoutException();

                document = new Document()
                        .append("id", transaction.id)
                        .append("cardId", transaction.payload.idCard)
                        .append("balance", transaction.payload.balance)
                        .append("pin", transaction.payload.pin)
                        .append("description", transaction.getCurrentStep())
                        .append("sagaStatus", transaction.sagaStatus)
                        .append("stepStatus", transaction.stepStatus)
                        .append("type", transaction.type)
                        .append("timeStamp", actualDate)
                        .append("version", transaction.version);
                logger.info("Document Transaction=" + getCollection().insertOne(document));
            }else{
                document = new Document()
                        .append("id", transaction.id)
                        .append("cardId", transaction.payload.idCard)
                        .append("balance", transaction.payload.balance)
                        .append("pin", transaction.payload.pin)
                        .append("description", transaction.getCurrentStep())
                        .append("sagaStatus", transaction.sagaStatus)
                        .append("stepStatus", transaction.stepStatus)
                        .append("type", transaction.type)
                        .append("timeStamp", new Date())
                        .append("version", transaction.version);
                logger.info("Document Transaction=" + getCollection().insertOne(document));
            }


        return Transaction.TRANSACTION_UPDATED;
    }

//    public TransactionService(MongoClient mongoClient) {
//        ConnectionString connectionString = new ConnectionString("mongodb+srv://odin:odinodin@cluster0.o2aax.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
//        MongoClientSettings settings = MongoClientSettings.builder()
//                .applyConnectionString(connectionString)
//                .build();
//        mongoClient = MongoClients.create(settings);
//        MongoDatabase database = mongoClient.getDatabase("test");
//
//        this.mongoClient = mongoClient;
//    }

    //    public String compensateAndUpdateBalance(Card card){
//        Document document = getCollection().find(eq("idCard", card.idCard)).first();
//        if (document != null) {
//            getCollection().updateOne(eq("idCard", card.idCard),
//                    new Document("$set", new Document("balance",document.getDouble("balance")+card.getValue())));
//        } else return Card.CARD_NOT_FOUND;
//        return Card.BALANCE_RESTORED;
//    }

}
