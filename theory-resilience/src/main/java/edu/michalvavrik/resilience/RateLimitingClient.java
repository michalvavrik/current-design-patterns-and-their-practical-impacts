package edu.michalvavrik.resilience;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "http://localhost:8081")
public interface RateLimitingClient extends RateLimiting {
}
