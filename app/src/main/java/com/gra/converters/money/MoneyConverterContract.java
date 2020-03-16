package com.gra.converters.money;

import android.app.Activity;

import com.gra.converters.money.model.Currency;

import java.util.List;

public interface MoneyConverterContract {

    interface View {
        void updateSingleValue(double convertedValue);

        void updateMoneyList();

        void updateCurrencySpinner(List<Currency> currencies);

        void updateTimestampForWhenCurrenciesWereDownloadedLast(long timestamp);

        void showError();

        void enableConvertButton();
    }

    interface Presenter {
        void convertInputMoneyToASpecificCurrency(double moneyInput, Currency toCurrency, Currency fromCurrency);

        void convertInputMoneyToAllCurrencies(double moneyInput);

        boolean validateInput(String inputToValidate);

        void getCurrencyMappings(Activity activity, String key);

        void initRetrofit();
    }
}
