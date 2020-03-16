package com.gra.converters.money.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

public class Currency implements Parcelable {

    private Long _id;
    private double rate;
    private String code;

    public Currency() {
    }

    public Currency(String code, double rate) {
        this.rate = rate;
        this.code = code;
    }

    private Currency(Parcel in) {
        this._id = in.readLong();
        this.rate = in.readDouble();
        this.code = in.readString();
    }

    public static ArrayList<Currency> generateCurrencies(HashMap<String, String> mappings, RateResponse response) {
        TreeMap<String, Double> rates = response.getRates();
        ArrayList<Currency> currencies = new ArrayList<>();
        for (String key : rates.keySet()) {
            currencies.add(new Currency(key, rates.get(key)));
        }

        return currencies;
    }


    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this._id);
        dest.writeDouble(this.rate);
        dest.writeString(this.code);
    }

    public Long get_id() {
        return _id;
    }

    public double getRate() {
        return rate;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", rate=" + rate +
                '}';
    }
}

