package org.acme.rest;

import org.acme.ksql.KsqlClientStreamQuery;
import org.acme.model.Transaction;
import org.acme.producer.FinalProducer;
import org.acme.producer.PinProducer;
import org.acme.producer.TransactionProducer;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TransactionResource {

    @Inject
    TransactionProducer producer;

    @Inject
    FinalProducer finalProducer;

//    @Inject
//    PinProducer pinProducer;

    @POST
    public Response send(Transaction transaction) {
        producer.sendTransactionToKafka(transaction);
        // Return an 202 - Accepted response.
        return Response.accepted().entity(transaction).build();
    }



    @POST
    @Path("/finalProducer")
    public Response sendFinalProducer(Transaction transaction) {
        //executeStreamJoin(transaction);
        //pinProducer.sendTransactionToKafka(transaction);
        finalProducer.sendTransactionToKafka(transaction);
        // Return an 202 - Accepted response.
        return Response.accepted().entity(transaction).build();
    }


    private void executeStreamJoin(Transaction transaction) {
        StringBuilder query =new StringBuilder( "SELECT * FROM join_authorization_stream");
        query.append(" where id='"+transaction.id+"'");
        query.append(" EMIT CHANGES;");
        KsqlClientStreamQuery.queryStream(query.toString());
    }
}