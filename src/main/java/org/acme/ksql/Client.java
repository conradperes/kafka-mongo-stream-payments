package org.acme.ksql;

import io.confluent.ksql.api.client.StreamedQueryResult;

import java.util.concurrent.CompletableFuture;

public interface Client {
    CompletableFuture<StreamedQueryResult> streamQuery(String sql);
}
