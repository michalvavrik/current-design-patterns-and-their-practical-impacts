package edu.michalvavrik.di;

import java.time.Instant;
import java.util.Random;

public class AbstractInstantiationBean {

    private final int random = new Random().nextInt();
    private final Instant instant = Instant.now();

    public int getRandom() {
        return random;
    }

    public Instant getInstant() {
        return instant;
    }
}
