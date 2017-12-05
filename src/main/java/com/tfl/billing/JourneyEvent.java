package com.tfl.billing;

import java.util.UUID;

public abstract class JourneyEvent {
    private final UUID cardId;
    private final UUID readerId;
    private final long time;

    /*
          - differentiate the type of journey
          JourneyStart -> true
          JourneyEnd -> false
      */
    private final boolean start;

    public JourneyEvent(UUID cardId, UUID readerId, boolean start) {
        this.cardId = cardId;
        this.readerId = readerId;
        this.time = System.currentTimeMillis();
        this.start = start;
    }

    // used for mock test
    public JourneyEvent(UUID cardId, UUID readerId, Clock clock, boolean start) {
        this.cardId = cardId;
        this.readerId = readerId;
        this.time = clock.now();
        this.start = start;
    }

    public UUID cardId() {
        return cardId;
    }

    public UUID readerId() {
        return readerId;
    }

    public long time() {
        return time;
    }

    public boolean isStart() {
        return start;
    }
}
