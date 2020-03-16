package com.gra.converters.money;

import com.gra.converters.money.model.Currency;

import java.text.DecimalFormat;

public class MoneyConverterPresenter implements MoneyConverterContract.Presenter {

    private MoneyConverterContract.View view;

    public MoneyConverterPresenter(MoneyConverterContract.View view) {
        this.view = view;
    }

    @Override
    public void convertInputMoneyToASpecificCurrency(double moneyInput, Currency toCurrency, Currency fromCurrency) {
        double calculatedAmount = Double.parseDouble(new DecimalFormat("##.###").format(toCurrency.getRate() * (1 / fromCurrency.getRate()) * moneyInput));
        view.updateSingleValue(calculatedAmount);
    }

    @Override
    public void convertInputMoneyToAllCurrencies(double moneyInput) {

    }

    @Override
    public boolean validateInput(String inputToValidate) {
        if (inputToValidate == null && !inputToValidate.isEmpty() && Double.parseDouble(inputToValidate) > 0) {
            view.showError();
            return false;
        }
        return true;
    }
}
