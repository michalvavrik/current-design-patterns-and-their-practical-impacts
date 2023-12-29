package edu.michalvavrik;

import io.quarkus.vertx.ConsumeEvent;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventBusMessageConsumer {

    @ConsumeEvent("my-event")
    public String myEventConsumer(String event) {
        return event + " Hey!";
    }

}
