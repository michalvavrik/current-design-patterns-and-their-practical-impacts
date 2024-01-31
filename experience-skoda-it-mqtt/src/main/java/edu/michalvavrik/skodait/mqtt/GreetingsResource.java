package edu.michalvavrik.skodait.mqtt;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;

@Path("/greetings")
public class GreetingsResource {

    @Inject
    @Channel("converted-greeting")
    Multi<String> greetings;

    @GET
    @Path("/stream")
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<String> stream() {
        return greetings;
    }
}
