package edu.michalvavrik.resilience;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("rate-limiting")
public interface RateLimiting {

    @GET
    String hello();

}
