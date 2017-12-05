package com.tfl.billing;

import java.util.UUID;

public class JourneyStart extends JourneyEvent {
    public JourneyStart(UUID cardId, UUID readerId) {
        super(cardId, readerId, true);
    }

    // used for mock test
    public JourneyStart(UUID cardId, UUID readerId, Clock clock) {
        super(cardId, readerId, clock, true);
    }
}
