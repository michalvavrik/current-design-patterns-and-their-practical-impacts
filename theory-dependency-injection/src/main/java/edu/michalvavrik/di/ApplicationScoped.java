package edu.michalvavrik.di;

import jakarta.inject.Inject;

@jakarta.enterprise.context.ApplicationScoped
public class ApplicationScoped extends AbstractInstantiationBean {

    @Inject
    Dependent dependent;

    @Inject
    Singleton singleton;

    public Dependent getDependent() {
        return dependent;
    }

    public Singleton getSingleton() {
        return singleton;
    }
}
