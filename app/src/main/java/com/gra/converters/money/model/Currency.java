package com.gra.converters.money.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Currency implements Parcelable {

    private Long _id;
    private double rate;
    private String code;
    private double convertedValue;

    public Currency() {
    }

    public Currency(String code, double rate) {
        this.rate = rate;
        this.code = code;
    }

    public Currency(String code, double rate, double convertedValue) {
        this.rate = rate;
        this.code = code;
        this.convertedValue = convertedValue;
    }

    private Currency(Parcel in) {
        this._id = in.readLong();
        this.rate = in.readDouble();
        this.code = in.readString();
        this.convertedValue = in.readDouble();
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
        dest.writeDouble(this.convertedValue);
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

    public double getConvertedValue() {
        return convertedValue;
    }

    public void setConvertedValue(double convertedValue) {
        this.convertedValue = convertedValue;
    }
}

