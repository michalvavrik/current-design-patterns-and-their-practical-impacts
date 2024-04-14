package edu.michalvavrik.callback;

import jakarta.enterprise.context.Dependent;

import java.util.function.Consumer;

@Dependent
public class EventProcessor {

    public void process(String message, Consumer<String> consumer) {
        String transformedMessage = message + " John!";
        consumer.accept(transformedMessage);
    }

}
