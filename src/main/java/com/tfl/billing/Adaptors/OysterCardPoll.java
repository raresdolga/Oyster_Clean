package com.tfl.billing.Adaptors;

import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.HashMap;
import java.util.Map;

public class OysterCardPoll {
    private static Map<Station, OysterCardReaderI> readers;

    static  {
        readers = new HashMap<>();
    }

    private OysterCardPoll() {

    }

    public static OysterCardReaderI atStation(Station station) {
        if (! readers.containsKey(station)) {
            readers.put(station, new OysterCardReaderAdapter(OysterReaderLocator.atStation(station)));
        }

        return readers.get(station);
    }
}
