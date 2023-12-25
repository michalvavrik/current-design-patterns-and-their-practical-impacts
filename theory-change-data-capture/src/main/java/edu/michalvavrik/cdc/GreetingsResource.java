package edu.michalvavrik.cdc;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Consumes(TEXT_PLAIN)
@Produces(TEXT_PLAIN)
@Path("greetings")
public class GreetingsResource {

    @Path("{id}")
    @GET
    public String get(@PathParam("id") long id) {
        return GreetingsEntity.<GreetingsEntity>findById(id).greeting;
    }

    @GET
    public Set<Long> list() {
        return GreetingsEntity.<GreetingsEntity>listAll().stream().map(s -> s.id).collect(Collectors.toSet());
    }

    @Transactional
    @POST
    public long create(String greeting) {
        Objects.requireNonNull(greeting);
        var entity = new GreetingsEntity();
        entity.greeting = greeting;
        entity.persist();
        return entity.id;
    }

    @Transactional
    @Path("{id}")
    @PUT
    public void update(@PathParam("id") long id, String greeting) {
        Objects.requireNonNull(greeting);
        var entity = GreetingsEntity.<GreetingsEntity>findById(id);
        if (entity.greeting.equals(greeting)) {
            return;
        }
        entity.greeting = greeting;
        entity.persist();
    }

    @Transactional
    @Path("{id}")
    @DELETE
    public boolean delete(@PathParam("id") long id) {
        return GreetingsEntity.deleteById(id);
    }
}
