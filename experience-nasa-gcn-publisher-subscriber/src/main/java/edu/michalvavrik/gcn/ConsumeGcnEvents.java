package edu.michalvavrik.gcn;

import io.smallrye.common.annotation.NonBlocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ConsumeGcnEvents {

    private static final Logger log = Logger.getLogger("edu.michalvavrik.gcn");

    @NonBlocking
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    @Incoming("gcn.classic.text.SWIFT_ACTUAL_POINTDIR")
    public void consumeGcnEvent(String event) {
        log.info("Incoming event " + event);
    }

}
