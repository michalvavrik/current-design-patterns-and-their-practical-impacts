package edu.michalvavrik;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;

@QuarkusTest
public class PublisherSubscriberTest {

    @Test
    public void testPublisherSubscriberUsingMutiny() {
        RestAssured.get("/publisher-subscriber").then().statusCode(200).body(is("Published response and Subscribed response"));
    }

    @Test
    public void testEventBus() {
        RestAssured.get("/event-bus").then().statusCode(200).body(is("Ho Hey!"));
    }

    @Test
    public void testJavaFlow() {
        RestAssured.get("/java-flow").then().statusCode(200).body(is("server-response-plus-client-response"));
    }

}
