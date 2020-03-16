package com.gra.converters.money.model;

import java.util.TreeMap;

public class RateResponse {
    private long timestamp;
//    private HashMap<String, Double> rates;
    private TreeMap<String, Double> rates;

    public long getTimestamp() {
        return timestamp;
    }

    public TreeMap<String, Double> getRates() {
        return rates;
    }
}
