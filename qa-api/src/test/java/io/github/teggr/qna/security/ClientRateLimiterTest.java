package io.github.teggr.qna.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.teggr.qna.api.ApiProperties;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ClientRateLimiterTest {
    @Test
    void enforcesPerClientFixedWindowAndResetsOnNextWindow() {
        ApiProperties properties = new ApiProperties();
        properties.setRequestsPerWindow(2);
        properties.setRateLimitWindowSeconds(60);
        MutableClock clock = new MutableClock(Instant.parse("2026-10-24T12:00:00Z"));
        ClientRateLimiter limiter = new ClientRateLimiter(properties, clock);

        assertTrue(limiter.allow("192.0.2.1"));
        assertTrue(limiter.allow("192.0.2.1"));
        assertFalse(limiter.allow("192.0.2.1"));
        assertTrue(limiter.allow("192.0.2.2"));
        clock.advanceSeconds(60);
        assertTrue(limiter.allow("192.0.2.1"));
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        private void advanceSeconds(long seconds) {
            instant = instant.plusSeconds(seconds);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
