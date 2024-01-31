package edu.michalvavrik.skodait.mqtt;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.reactive.messaging.annotations.Broadcast;

@ApplicationScoped
public class GreetingConverter {

    private static final Logger LOG = Logger.getLogger(GreetingConverter.class);

    @ConfigProperty(name = "quarkus.application.name")
    String applicationName;

    @Incoming("greetings")
    @Outgoing("converted-greeting")
    @Broadcast
    public String process(byte[] rawGreeting) {
        String greeting = new String(rawGreeting) + "!";
        LOG.infof("%s transformed greeting: %s ", applicationName, greeting);
        return greeting;
    }

}
