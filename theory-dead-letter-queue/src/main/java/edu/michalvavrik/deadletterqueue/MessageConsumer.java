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
public class MessageConsumer {

    private static final Logger LOG = Logger.getLogger(MessageProducer.class.getName());

    private final ConcurrentLinkedQueue<Integer> messages = new ConcurrentLinkedQueue<>();

    @Inject
    @Channel("dead-letter-producer")
    Emitter<Integer> deadLetterProducer;

    @Incoming("message")
    public void process(Integer msg) {
        if (isWrongMessage(msg)) {
            deadLetterProducer.send(msg);
        } else {
            LOG.info("processing message: " + msg);
            this.messages.add(msg);
        }
    }

    private static boolean isWrongMessage(Integer msg) {
        return msg == 20;
    }

    public Queue<Integer> getMessages() {
        return messages;
    }
}
