package edu.michalvavrik;

import io.vertx.ext.web.Router;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;

public class CdiEvents {

    record CustomEvent(String name) {}
    private static volatile CustomEvent incomingEvent = null;

    void observeEvent(@Observes CustomEvent customEvent) {
        incomingEvent = customEvent;
    }

    void setUpRoute(@Observes Router router, Event<CustomEvent> event) {
        router.route("/cdi-events").handler(routingContext -> {
            event.fire(new CustomEvent("CDI"));
            if (incomingEvent == null) {
                routingContext.fail(new IllegalStateException("Synchronous event should had arrived by now"));
            } else {
                routingContext.end(incomingEvent.name());
            }
        });
    }

}
