package edu.michalvavrik.di;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ClientProxy;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
public class DependencyInjectionTest {

    @Inject
    Dependent dependent;

    @Inject
    ApplicationScoped applicationScoped;

    @Inject
    Singleton singleton;

    @Order(1)
    @Test
    public void testApplicationScopedBean() {
        // application scoped is basically a proxy for the original bean,
        // and it is only created when needed, hence,
        assertNotNull(applicationScoped);
        assertNull(applicationScoped.dependent);
        assertNotNull(applicationScoped.getDependent());
        assertNull(applicationScoped.dependent);
        var actualBean = ClientProxy.unwrap(applicationScoped);
        assertNotNull(actualBean.dependent);
    }

    @Test
    public void testSingletonBean() {
        // singleton is created when injected, therefore it must had been instantiated before application scoped
        assertTrue(singleton.getInstant().compareTo(applicationScoped.getInstant()) < 0);
        assertNotEquals(singleton.getDependent().getRandom(), applicationScoped.getDependent().getRandom());
        assertTrue(singleton.getDependent().getInstant().compareTo(applicationScoped.getDependent().getInstant()) < 0);
        // singleton is created just once per application
        assertEquals(singleton.getRandom(), applicationScoped.getSingleton().getRandom());
    }

    @Order(3)
    @Test
    public void testDependentBean() {
        // dependent bean is created for each injection point, hence we expect all instances to differ
        assertNotNull(dependent);
        assertNotNull(Observer.dependent);
        assertNotEquals(dependent.getRandom(), Observer.dependent.getRandom());
        assertNotNull(applicationScoped.getDependent());
        assertNotEquals(applicationScoped.getDependent().getRandom(), dependent.getRandom());
        assertNotEquals(applicationScoped.getDependent().getRandom(), Observer.dependent.getRandom());
    }

    @Order(4)
    @Test
    public void testRequestScopedBean() {
        // method observer gets new activated CDI request scope if not activate already
        int random = Observer.requestScopedRandom;
        // let's create a new request scope and assure it's a different bean
        var container = Arc.container();
        container.requestContext().terminate();
        try {
            container.requestContext().activate();
            int newRandom = Arc.container().instance(RequestScoped.class).get().getRandom();
            assertNotEquals(random, newRandom);
        } finally {
            container.requestContext().terminate();
        }
    }

}
