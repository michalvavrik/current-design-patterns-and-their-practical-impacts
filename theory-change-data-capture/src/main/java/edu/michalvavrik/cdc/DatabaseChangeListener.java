package edu.michalvavrik.cdc;

import io.debezium.config.Configuration;
import io.debezium.connector.postgresql.PostgresConnector;
import io.debezium.connector.postgresql.PostgresConnectorConfig;
import io.debezium.embedded.EmbeddedEngine;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Vertx;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.storage.MemoryOffsetBackingStore;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.util.function.Function;

@Singleton
public class DatabaseChangeListener {

    record DbConfig(String hostname, int port, String dbName) {}
    private Runnable shutdownHook;

    void startupDebezium(@Observes StartupEvent event, Vertx vertx, Event<DataChangeEvent> changeEvent,
                         @ConfigProperty(name = "quarkus.datasource.jdbc.url") String jdbcUrl,
                         @ConfigProperty(name = "quarkus.datasource.password") String dsPassword,
                         @ConfigProperty(name = "quarkus.datasource.username") String dsUsername) {
        var dbConfig = parseDbConfig(jdbcUrl);
        Configuration config = Configuration.empty()
                .withSystemProperties(Function.identity()).edit()
                .with(EmbeddedEngine.CONNECTOR_CLASS, PostgresConnector.class)
                .with(EmbeddedEngine.ENGINE_NAME, "greetings-changes-engine")
                .with(EmbeddedEngine.OFFSET_STORAGE, MemoryOffsetBackingStore.class)
                .with("name", "greetings-changes-engine")
                .with("database.hostname", dbConfig.hostname())
                .with("database.port", dbConfig.port())
                .with("database.user", dsUsername)
                .with("database.password", dsPassword)
                .with("topic.prefix", "dbserver1")
                .with("database.dbname", dbConfig.dbName())
                .with("table.include.list", "public.greetingsentity")
                .with("plugin.name", "pgoutput")
                .with(PostgresConnectorConfig.SNAPSHOT_MODE, PostgresConnectorConfig.SnapshotMode.NEVER)
                .build();

        var engine = new EmbeddedEngine.EngineBuilder()
                .using(config.asProperties())
                .notifying(o -> {
                    if (o instanceof SourceRecord sourceRecord && sourceRecord.value() != null) {
                        Struct payload = ((Struct) sourceRecord.value()).getStruct("after");
                        if (payload != null) {
                            long id = payload.getInt64("id");
                            String greeting = payload.getString("greeting");
                            changeEvent.fireAsync(new DataChangeEvent(id, greeting));
                        }
                    }
                })
                .build();
        shutdownHook = () -> {
            try {
                engine.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        vertx.executeBlocking(() -> {
            engine.run();
            return null;
        });
    }

    private static DbConfig parseDbConfig(String jdbcUrl) {
        String withoutProtocol = jdbcUrl.substring("jdbc:postgresql://".length());
        String withoutQueryParams = withoutProtocol.substring(0, withoutProtocol.indexOf('?'));
        int slashIdx = withoutQueryParams.indexOf('/');
        String dbName = withoutQueryParams.substring(slashIdx + 1);
        String hostToPort = withoutQueryParams.substring(0, slashIdx);
        int colonIdx = hostToPort.indexOf(":");
        String host = hostToPort.substring(0, colonIdx);
        int port = Integer.parseInt(hostToPort.substring(colonIdx + 1));
        return new DbConfig(host, port, dbName);
    }

    void shutdownDebezium(@Observes ShutdownEvent event) {
        if (shutdownHook != null) {
            shutdownHook.run();
        }
    }

}
