package edu.michalvavrik.resilience;

import io.smallrye.faulttolerance.api.RateLimit;

public class RateLimitingResource implements RateLimiting {

    @RateLimit(value = 2, window = 10)
    @Override
    public String hello() {
        return "hello";
    }
}
