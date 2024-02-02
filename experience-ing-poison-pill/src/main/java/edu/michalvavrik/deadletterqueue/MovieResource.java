package edu.michalvavrik.deadletterqueue;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@Path("/movies")
public class MovieResource {
    private static final Logger LOGGER = Logger.getLogger(MovieResource.class);

    @Channel("movies")
    Emitter<Movie> emitter;

    @POST
    public Response enqueueMovie(Movie movie) {
        LOGGER.infof("Sending movie %s to Kafka", movie.getTitle());
        emitter.send(movie);
        emitter.send(new Movie("dead-letter", 1));
        return Response.accepted().build();
    }

}
