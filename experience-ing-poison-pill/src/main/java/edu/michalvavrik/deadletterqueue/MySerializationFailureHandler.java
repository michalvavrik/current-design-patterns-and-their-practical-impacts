package edu.michalvavrik.deadletterqueue;

import io.smallrye.common.annotation.Identifier;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.SerializationFailureHandler;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.common.header.Headers;
import org.jboss.logging.Logger;

import java.util.function.Function;

@ApplicationScoped
@Identifier("failure-fallback")
public class MySerializationFailureHandler
        implements SerializationFailureHandler<Movie> {

    private static final Logger LOG = Logger.getLogger(MySerializationFailureHandler.class.getName());

    @Override
    public byte[] decorateSerialization(Uni<byte[]> serialization, String topic, boolean isKey, String serializer, Movie data, Headers headers) {
        return serialization
                .onItem().transform(new Function<byte[], byte[]>() {
                    @Override
                    public byte[] apply(byte[] bytes) {
                        if (data.getTitle().equals("dead-letter")) {
                            return "dead-pill".getBytes();
                        }
                        return bytes;
                    }
                })
                .onFailure().recoverWithNull()
                .await().indefinitely();
    }
}