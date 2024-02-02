package edu.michalvavrik.deadletterqueue;

import io.smallrye.common.annotation.Identifier;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.kafka.DeserializationFailureHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.kafka.common.header.Headers;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.time.Duration;

@ApplicationScoped
@Identifier("failure-retry")
public class MyDeserializationFailureHandler
        implements DeserializationFailureHandler<Movie> {

    @Inject
    @Channel("dead-letter-producer")
    Emitter<Integer> deadLetterProducer;

    @Override
    public Movie decorateDeserialization(Uni<Movie> deserialization, String topic, boolean isKey, String deserializer, byte[] data, Headers headers) {
        return deserialization
                .onFailure().invoke(new Runnable() {
                    @Override
                    public void run() {
                        if (new String(data).equals("dead-pill")) {
                            deadLetterProducer.send(333);
                        }
                    }
                })
                .onFailure().recoverWithItem(new Movie("ignored", 1))
                .await().atMost(Duration.ofMillis(200));
    }
}