package com.gra.converters.money.services.dto;

import java.util.TreeMap;

/**
 * Domain response from external Currency API
 */
public class RateResponse {
    private long timestamp;
    private TreeMap<String, Double> rates;

    public long getTimestamp() {
        return timestamp;
    }

    public TreeMap<String, Double> getRates() {
        return rates;
    }
}
