package io.quarkiverse.hivemqclient.test.smallrye;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.annotations.Broadcast;

@ApplicationScoped
public class GreetingConverter {

    private static final Logger LOG = Logger.getLogger(GreetingConverter.class);

    @Incoming("greetings")
    @Outgoing("converted-greeting")
    @Broadcast
    public String process(byte[] rawGreeting) {
        String greeting = new String(rawGreeting) + "!";
        LOG.infof("Transformed greeting: %s ", greeting);
        return greeting;
    }

}
