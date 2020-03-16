package com.gra.converters.money.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

 import com.gra.converters.money.model.Currency;

import java.util.List;

import nl.qbusict.cupboard.convert.EntityConverter;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SQLHelper";
    private static final String DATABASE_NAME = "Currenciesq.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper instance = null;

    private Context mContext;

    static {
        cupboard().register(Currency.class);
    }

    private DatabaseHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = c;
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }

    public void addCurrency(Currency currency) {
        cupboard().withDatabase(getWritableDatabase()).put(currency);
    }

    public void updateCurrency(Currency currency) {
        EntityConverter<Currency> currencyEntityConverter = cupboard().getEntityConverter(Currency.class);
        ContentValues newValues = new ContentValues();
        currencyEntityConverter.toValues(currency, newValues);
        cupboard().withDatabase(getWritableDatabase()).update(Currency.class, newValues, "WHERE name=? AND code=?", currency.getCode(), currency.getCode());
    }

    public boolean isDatabaseEmpty() {
        return cupboard().withDatabase(getReadableDatabase()).query(Currency.class).list().isEmpty();
    }

    public void addCurrencies(List<Currency> currencies) {
        boolean isEmpty = isDatabaseEmpty();
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (Currency c : currencies) {
                if (isEmpty) {
                    addCurrency(c);
                } else {
                    updateCurrency(c);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error adding currencies to localstorage " + e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
    }

    public List<Currency> getCurrencies() {
        List<Currency> currencies = null;
        try {
            currencies = cupboard().withDatabase(getReadableDatabase()).query(Currency.class).list();
        } catch (Exception e) {
            Log.e(TAG, "Could not fetch currencies" + e.getLocalizedMessage());
        }
        return currencies;
    }
}
