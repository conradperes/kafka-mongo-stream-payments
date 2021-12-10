package org.acme.ksql;
import io.confluent.ksql.api.client.*;
import io.confluent.ksql.api.client.Client;
import lombok.SneakyThrows;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class KsqlClientStreamQuery {

    private static String KSQLDB_SERVER_HOST = "localhost";
    private static int KSQLDB_SERVER_HOST_PORT = 8088;
    private static final Logger logger = Logger.getLogger(KsqlClientStreamQuery.class);
    public static void main(String[] args) {
        queryStream("SELECT * FROM join_authorization_stream EMIT CHANGES;");
    }

    @SneakyThrows
    public static String queryStream(String query) {
        ClientOptions options = ClientOptions.create()
                .setHost(KSQLDB_SERVER_HOST)
                .setPort(KSQLDB_SERVER_HOST_PORT);
        Client client = Client.create(options);
        logger.info("QUERY="+query);
        Map<String, Object> properties = Collections.singletonMap("auto.offset.reset", "earliest");
        client.executeStatement(query, properties).get();
        return null;
    }
}