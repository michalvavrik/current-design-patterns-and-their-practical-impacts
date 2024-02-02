package edu.michalvavrik.deadletterqueue;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ApplicationScoped
public class DeadLetterConsumer {

    private static final Logger LOG = Logger.getLogger(MessageProducer.class.getName());

    private final ConcurrentLinkedQueue<Integer> messages = new ConcurrentLinkedQueue<>();

    @Inject
    @Channel("generated-message")
    Emitter<Integer> retryProducer;

    @Incoming("dead-letter-msg")
    public void process(Integer msg) {
        LOG.info("processing dead-letter message: " + msg);
        this.messages.add(msg);
        this.retryProducer.send(msg + 1);
    }

    public Queue<Integer> getMessages() {
        return messages;
    }
}
