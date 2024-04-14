package edu.michalvavrik.callback;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.websocket.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnMessage;

import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@QuarkusTest
public class CallbackTest {

    private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

    @TestHTTPResource("/greeting")
    URI uri;

    @Test
    public void test() throws Exception {
        try (Session session = ContainerProvider.getWebSocketContainer().connectToServer(Client.class, uri)) {
            session.getAsyncRemote().sendText("Hi");
            String callbackResponse = MESSAGES.poll(10, TimeUnit.SECONDS);
            Assertions.assertEquals("Hi John!", callbackResponse);
        }
    }

    @ClientEndpoint
    public static class Client {

        @OnMessage
        void message(String msg) {
            MESSAGES.add(msg);
        }

    }
}
