package com.tfl.billing.Adaptors;

import com.oyster.OysterCard;
import com.oyster.OysterCardReader;
import com.oyster.ScanListener;

import java.util.UUID;


/*
    Adapter for OysterCardReader
 */
public class OysterCardReaderAdapter implements OysterCardReaderI {
    private OysterCardReader cardReader;

    public OysterCardReaderAdapter(OysterCardReader cardReader){
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
        return cardReader.id();
    }
}
