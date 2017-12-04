package com.tfl.billing.Adaptors;

import com.oyster.OysterCard;
import com.oyster.ScanListener;

import java.util.UUID;


public interface OysterCardReaderI {
    void register(ScanListener scanListener);
    void touch(OysterCard card);
    UUID id();
}
