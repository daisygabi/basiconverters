package com.gra.converters.money;

import android.app.Activity;

import com.gra.converters.money.model.Currency;

import java.util.List;

public interface MoneyConverterContract {

    interface View {
        void updateCurrencyListDetails(List<Currency> currencies);

        void updateCurrencySpinner(List<Currency> currencies);

        void updateTimestampForWhenCurrenciesWereDownloadedLast(long timestamp);

        void showError();

        void enableConvertButton();
    }

    interface Presenter {
        void convertInputMoneyToAllCurrencies(double moneyInput, List<Currency> currencies, Currency fromCurrency);

        boolean validateInput(String inputToValidate);

        void getCurrencyMappings(Activity activity, String key);

        void initRetrofit();
    }
}
