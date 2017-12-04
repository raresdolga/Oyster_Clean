package com.tfl.billing.Adaptors;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;

import java.util.UUID;

/**
 * Created by Rares on 25.11.2017.
 */
public class OysterCardAdapterReader implements OysterCardReaderI {
    OysterCardReader cardReader = null;
    public OysterCardAdapterReader(OysterCardReader cardReader){
        this.cardReader = cardReader;
    }

    @Override
    public void register(ScanListener scanListener) {
        cardReader.register(scanListener);
    }

    @Override
    public void touch(OysterCard card) {
        cardReader.touch(card);
    }

    @Override
    public UUID id() {
        return null;
    }
}
