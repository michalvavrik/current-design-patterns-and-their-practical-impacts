package io.quarkiverse.hivemqclient.test.smallrye;

import java.time.Duration;

import io.smallrye.mutiny.subscription.FixedDemandPacer;
import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class GreetingGenerator {

    public static final String[] EXPECTED_GREETINGS = new String[]{"Ahoj", "Hello", "Hi", "Holla", "Nazdar"};

    @Outgoing("greeting-topic")
    public Multi<String> generate() {
        return Multi
                .createFrom()
                .items(EXPECTED_GREETINGS)
                .paceDemand()
                .using(new FixedDemandPacer(1L, Duration.ofSeconds(1)));
    }

}
