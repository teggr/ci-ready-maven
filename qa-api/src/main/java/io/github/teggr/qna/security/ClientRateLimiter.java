package io.github.teggr.qna.security;

import io.github.teggr.qna.api.ApiProperties;
import java.time.Clock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientRateLimiter {
    private static final int MAX_TRACKED_CLIENTS = 10000;

    private final ApiProperties properties;
    private final Clock clock;
    private final Map<String, Window> windows = new HashMap<>();

    @Autowired
    public ClientRateLimiter(ApiProperties properties) {
        this(properties, Clock.systemUTC());
    }

    ClientRateLimiter(ApiProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public synchronized boolean allow(String clientAddress) {
        long now = clock.instant().getEpochSecond();
        long windowNumber = now / properties.getRateLimitWindowSeconds();
        Window window = windows.get(clientAddress);
        if (window == null || window.number() != windowNumber) {
            pruneExpired(windowNumber);
            if (windows.size() >= MAX_TRACKED_CLIENTS && !windows.containsKey(clientAddress)) {
                return false;
            }
            window = new Window(windowNumber, 0);
        }
        if (window.count() >= properties.getRequestsPerWindow()) {
            windows.put(clientAddress, window);
            return false;
        }
        windows.put(clientAddress, new Window(windowNumber, window.count() + 1));
        return true;
    }

    private void pruneExpired(long currentWindow) {
        Iterator<Window> iterator = windows.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().number() < currentWindow) {
                iterator.remove();
            }
        }
    }

    private record Window(long number, int count) {
    }
}
