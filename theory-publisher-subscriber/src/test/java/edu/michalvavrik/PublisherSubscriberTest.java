package edu.michalvavrik;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
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

}
