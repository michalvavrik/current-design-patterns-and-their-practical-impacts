package io.quarkiverse.hivemqclient.test.smallrye;

import java.time.Duration;

import io.quarkus.runtime.ShutdownEvent;
import io.smallrye.mutiny.subscription.FixedDemandPacer;
import jakarta.enterprise.context.ApplicationScoped;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import io.smallrye.mutiny.Multi;

@Singleton
public class GreetingGenerator {

    public static final String[] EXPECTED_GREETINGS = new String[]{"Ahoj", "Hello", "Hi", "Holla", "Nazdar"};
    private volatile boolean appShutdown = false;

    void observerShutdown(@Observes ShutdownEvent event) {
        // not sure if this is necessary, but this emitting is probably not good by design anyway, so...
        appShutdown = true;
    }

    @Outgoing("greeting-topic")
    public Multi<String> generate() {
        return Multi
                .createFrom()
                .<Integer, String>generator(() -> 0, (idx, emitter) -> {
                    if (appShutdown) {
                        emitter.complete();
                        return 0;
                    } else {
                        emitter.emit(EXPECTED_GREETINGS[idx]);
                        if (++idx == EXPECTED_GREETINGS.length) {
                            return 0;
                        } else {
                            return idx;
                        }

                    }
                })
                .paceDemand()
                .using(new FixedDemandPacer(1L, Duration.ofSeconds(1)));
    }

}
