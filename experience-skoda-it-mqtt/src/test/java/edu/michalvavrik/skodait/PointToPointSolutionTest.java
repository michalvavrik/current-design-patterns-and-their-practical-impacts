package edu.michalvavrik.skodait;

import io.quarkus.test.bootstrap.RestService;
import io.quarkus.test.scenarios.QuarkusScenario;
import io.quarkus.test.services.QuarkusApplication;

@QuarkusScenario
public class PointToPointSolutionTest {

    @QuarkusApplication
    static RestService point1 = new RestService();

    @QuarkusApplication
    static RestService point2 = new RestService();

    @QuarkusApplication
    static RestService point3 = new RestService();

    @QuarkusApplication
    static RestService point4 = new RestService();

}
