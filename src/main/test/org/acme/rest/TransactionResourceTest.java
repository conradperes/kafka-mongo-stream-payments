package org.acme.rest;


//import io.quarkus.test.junit.QuarkusTest;
//import io.quarkus.test.junit.mockito.InjectMock;
//import org.acme.model.Card;
//import org.acme.model.Transaction;
//import org.acme.service.TransactionService;
//
//import org.jboss.logging.Logger;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//
//import javax.inject.Inject;
//import javax.ws.rs.core.Response;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;

//@QuarkusTest
class TransactionResourceTest {

//    @InjectMock
//    TransactionService transactionService;
//    @Inject
//    TransactionResource resource;
//    private Transaction transaction;
//    private Card card;
//    private final Logger logger = Logger.getLogger(TransactionResourceTest.class);
//
//    @BeforeEach
//    void setUp(){
//        card =  new Card("1", "1000","1234");
//        transaction = new Transaction("73707ad2-0732-4592-b7e2-79b07c745e45", "initial", card, "STARTED", "SUCEEDEED","order-placement", "0" );
//    }
//
//    @Test
//    void
//
//    sendFinalProducer() throws  Exception{
//        Mockito.when(transactionService.insertTransaction(transaction))
//                .thenReturn("AcknowledgedInsertOneResult{insertedId=BsonObjec\n" +
//                        "tId{value=61ae72104b68af2f11568a8e}}");
//        Response response = resource.send(transaction);
//        assertNotNull(response);
//        logger.info("Response Status code = " + response.getStatus());
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    }
}