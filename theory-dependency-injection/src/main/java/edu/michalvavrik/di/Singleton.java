package edu.michalvavrik.di;

import jakarta.inject.Inject;

@jakarta.inject.Singleton
public class Singleton extends AbstractInstantiationBean {

    @Inject
    Dependent dependent;

    public Dependent getDependent() {
        return dependent;
    }
}
