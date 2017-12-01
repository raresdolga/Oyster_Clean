package com.tfl.billing.Adaptors;

import com.oyster.OysterCard;
import com.oyster.ScanListener;

import java.util.UUID;

/**
 * Created by Rares on 25.11.2017.
 */
public interface OysterCardReaderI {
    void register(ScanListener scanListener);
    void touch(OysterCard card);
    UUID id();
}
