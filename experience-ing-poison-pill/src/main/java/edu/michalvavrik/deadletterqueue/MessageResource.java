package edu.michalvavrik.deadletterqueue;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class MessageResource {

    @Inject
    MessageConsumer messageConsumer;

    @Inject
    DeadLetterConsumer deadLetterConsumer;

    @GET
    @Path("/message")
    @Produces(MediaType.TEXT_PLAIN)
    public Response message() {
        return Response.ok().entity(messageConsumer.getMessages()).build();
    }


    @GET
    @Path("/dead-letter-msg")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deadLetterMsg() {
        return Response.ok().entity(deadLetterConsumer.getMessages()).build();
    }

}
