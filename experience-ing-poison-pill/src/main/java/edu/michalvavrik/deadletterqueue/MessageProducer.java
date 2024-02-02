package edu.michalvavrik.deadletterqueue;

import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import java.time.Duration;

@ApplicationScoped
public class MessageProducer {

    private static final int TEN = 10;
    private static final int HUNDRED = 100;
    private static final Logger LOG = Logger.getLogger(MessageProducer.class.getName());

    @Outgoing("generated-message")
    public Multi<Integer> generate() {
        LOG.info("started generating messages");
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
                .onOverflow().drop()
                .map(tick -> ((tick.intValue() * TEN) % HUNDRED) + TEN);
    }
}
