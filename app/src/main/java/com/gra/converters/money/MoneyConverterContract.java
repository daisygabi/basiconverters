package com.gra.converters.money;

import com.gra.converters.money.model.Currency;

public interface MoneyConverterContract {

    interface View {
        void updateSingleValue(double convertedValue);

        void updateMoneyList();

        void showError();
    }

    interface Presenter {
        void convertInputMoneyToASpecificCurrency(double moneyInput, Currency toCurrency, Currency fromCurrency);

        void convertInputMoneyToAllCurrencies(double moneyInput);

        boolean validateInput(String inputToValidate);
    }
}
