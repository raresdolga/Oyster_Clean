package com.tfl.billing;

import com.tfl.billing.adaptors.OysterCardReaderAdapter;
import com.tfl.billing.adaptors.OysterCardReaderI;
import com.tfl.underground.OysterReaderLocator;
import com.tfl.underground.Station;

import java.util.HashMap;
import java.util.Map;


/*
    Variation of ProxyPattern

    Creates Adapters for OysterCardReader and maintains them in a Map for later retrieval
 */
public class OysterCardAdaptersPoll {
    private static Map<Station, OysterCardReaderI> readers;

    static  {
        readers = new HashMap<>();
    }

    private OysterCardAdaptersPoll() {

    }

    public static OysterCardReaderI atStation(Station station) {
        if (! readers.containsKey(station)) {
            readers.put(station, new OysterCardReaderAdapter(OysterReaderLocator.atStation(station)));
        }

        return readers.get(station);
    }
}
