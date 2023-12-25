package edu.michalvavrik.cdc;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class ChangesStorage {
    private final Map<Long, String> idToData = new ConcurrentHashMap<>();

    void observes(@ObservesAsync DataChangeEvent event) {
        idToData.put(event.id(), event.data());
    }

    String getById(Long id) {
        return idToData.get(id);
    }

    void clear() {
        idToData.clear();
    }
}
