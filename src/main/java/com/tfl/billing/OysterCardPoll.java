package com.tfl.billing;

import com.tfl.billing.Adaptors.OysterCardReaderAdapter;
import com.tfl.billing.Adaptors.OysterCardReaderI;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.HashMap;
import java.util.Map;


/*
    Variation of ProxyPattern

    Creates Adapters for OysterCardReader and maintains them in a Map for later retrieval
 */
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
