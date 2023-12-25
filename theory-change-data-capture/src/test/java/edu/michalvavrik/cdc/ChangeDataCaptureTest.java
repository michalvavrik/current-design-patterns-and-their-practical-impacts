package edu.michalvavrik.cdc;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@QuarkusTest
public class ChangeDataCaptureTest {

    @Inject
    ChangesStorage changesStorage;

    @BeforeEach
    public void clearChanges() {
        changesStorage.clear();
    }

    @Order(1)
    @Test
    public void testCreate() {
        var primaryKey = Long.parseLong(RestAssured.given().body("Hello").post("greetings").then().statusCode(200)
                .extract().asString());
        RestAssured.get("greetings/" + primaryKey).then().statusCode(200).body(Matchers.is("Hello"));
        Awaitility.await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> Assertions.assertEquals("Hello", changesStorage.getById(primaryKey)));
    }

    @Order(2)
    @Test
    public void testUpdate() {
        var primaryKey = RestAssured.get("greetings").body().jsonPath().getLong("[0]");
        RestAssured.given().body("Hi").put("greetings/" + primaryKey).then().statusCode(204);
        RestAssured.get("greetings/" + primaryKey).then().statusCode(200).body(Matchers.is("Hi"));
    }

    @Order(3)
    @Test
    public void testDelete() {
        var primaryKey = RestAssured.get("greetings").body().jsonPath().getLong("[0]");
        RestAssured.delete("greetings/" + primaryKey).then().statusCode(200).body(Matchers.is("true"));
        RestAssured.get("greetings").then().statusCode(200).body(Matchers.is("[]"));
    }
}
