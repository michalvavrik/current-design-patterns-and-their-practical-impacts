package edu.michalvavrik.callback;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Singleton
@ServerEndpoint("/greeting")
public class GreetingsEndpoint {

    @Inject
    EventProcessor eventProcessor;

    private volatile Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) throws ExecutionException, InterruptedException, TimeoutException {
        var callback = new Consumer<String>() {
            @Override
            public void accept(String s) {
                try {
                    session.getAsyncRemote().sendText(s).get(2, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        eventProcessor.process(message, callback);
    }

}
