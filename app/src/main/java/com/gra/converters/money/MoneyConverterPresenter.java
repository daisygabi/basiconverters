package com.gra.converters.money;

import android.app.Activity;
import android.util.Log;

import com.gra.converters.money.model.Currency;
import com.gra.converters.money.model.CurrencyService;
import com.gra.converters.money.model.RateResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.ContentValues.TAG;

public class MoneyConverterPresenter implements MoneyConverterContract.Presenter {

    private MoneyConverterContract.View view;
    private CurrencyService currencyService;

    public MoneyConverterPresenter(MoneyConverterContract.View view) {
        this.view = view;
    }

    @Override
    public void convertInputMoneyToAllCurrencies(double moneyInput, List<Currency> currencies, Currency fromCurrency) {
        for(Currency currency : currencies) {
            double calculatedAmount = Double.parseDouble(new DecimalFormat("##.###").format(currency.getRate() * (1 / fromCurrency.getRate()) * moneyInput));
            currency.setConvertedValue(calculatedAmount);
        }
        view.updateCurrencyListDetails(currencies);
    }

    @Override
    public boolean validateInput(String inputToValidate) {
        try {
            double amount = Double.parseDouble(inputToValidate);
            if (amount <= 0) {
                view.showError();
                return false;
            }
        } catch (NumberFormatException ex) {
            view.showError();
            return false;
        }
        return true;
    }

    @Override
    public void getCurrencyMappings(final Activity activity, final String key) {
        currencyService.getCurrencyMappings(key, new Callback<HashMap<String, String>>() {
            @Override
            public void success(HashMap<String, String> responseMap, Response response) {
                currencyService.getRates(key, new Callback<RateResponse>() {
                    @Override
                    public void success(RateResponse rateResponse, Response response) {
                        view.updateTimestampForWhenCurrenciesWereDownloadedLast(rateResponse.getTimestamp());

                        TreeMap<String, Double> ratesMap = rateResponse.getRates();
                        List<Currency> allCurrencies = new ArrayList<>();

                        for (Map.Entry<String, Double> entry : ratesMap.entrySet()) {
                            Currency currency = new Currency(entry.getKey(), entry.getValue());
                            allCurrencies.add(currency);
                        }
                        view.updateCurrencySpinner(allCurrencies);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, error.getLocalizedMessage());
            }
        });
    }

    @Override
    public void initRetrofit() {
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://openexchangerates.org/api")
                .build();

        currencyService = adapter.create(CurrencyService.class);
    }
}
