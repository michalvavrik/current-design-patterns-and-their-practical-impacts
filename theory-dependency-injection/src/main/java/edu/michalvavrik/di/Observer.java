package edu.michalvavrik.di;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;

public class Observer {

    public static int requestScopedRandom;
    public static Dependent dependent;

    void observe(@Observes StartupEvent ev, RequestScoped requestScoped, Dependent dependent) {
        Observer.requestScopedRandom = requestScoped.getRandom();
        Observer.dependent = dependent;
    }

}
