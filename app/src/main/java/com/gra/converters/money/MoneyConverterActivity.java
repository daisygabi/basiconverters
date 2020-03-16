package com.gra.converters.money;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gra.converters.R;
import com.gra.converters.money.adapter.CurrencyAdapter;
import com.gra.converters.money.database.DatabaseHelper;
import com.gra.converters.money.model.Currency;
import com.gra.converters.money.model.CurrencyService;
import com.gra.converters.money.model.RateResponse;
import com.gra.converters.utils.ActivityHelper;
import com.gra.converters.utils.ErrorDialogFragment;

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

public class MoneyConverterActivity extends Activity implements MoneyConverterContract.View, View.OnClickListener {

    private Spinner currencyTypesSpinner;
    private EditText moneyInput;
    private Button convertMoneyBtn;
    private TextView resultTxt;
    private MoneyConverterPresenter presenter;
    private Currency fromCurrency;
    private Currency toCurrency = new Currency("RON", 4.2984);

    private CurrencyService currencyService;
    private DatabaseHelper databaseHelper;
    private CurrencyAdapter mCurrencyAdapter;
    private String key;
    public static final String TIMESTAMP_KEY = "TIMESTAMP_KEY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_converter_activity);
        currencyTypesSpinner = findViewById(R.id.currencyTypes);
        moneyInput = findViewById(R.id.moneyInput);
        convertMoneyBtn = findViewById(R.id.convertMoneyBtn);
        resultTxt = findViewById(R.id.resultTxt);
        key = getString(R.string.key);
        presenter = new MoneyConverterPresenter(this);

        initAdapter();
        initSpinnerOnSelect();
        convertMoneyBtn.setOnClickListener(this);
    }

    private void initAdapter() {
        final RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint("http://openexchangerates.org/api")
                .build();

        currencyService = adapter.create(CurrencyService.class);
        databaseHelper = DatabaseHelper.getInstance(this);
        if (databaseHelper.isDatabaseEmpty()) {
            downloadInformationIfNetworkIsAvailable();
        } else {
            mCurrencyAdapter = new CurrencyAdapter(this);
            currencyTypesSpinner.setAdapter(mCurrencyAdapter);
        }
    }

    private void initSpinnerOnSelect() {
        currencyTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                fromCurrency = (Currency) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }



    @Override
    public void updateSingleValue(double convertedValue) {
        resultTxt.setText(String.valueOf(convertedValue));
    }

    @Override
    public void updateMoneyList() {

    }

    @Override
    public void showError() {
        Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_LONG).show();
    }

    private void downloadInformationIfNetworkIsAvailable() {
        if (!isNetworkAvailable()) {
            ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.title_error_no_network), getString(R.string.message_error_no_network));
            fragment.show(getFragmentManager(), "FRAGMENT_ERROR");
        } else {
            getCurrenciesFromService();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void getCurrenciesFromService() {
        convertMoneyBtn.setEnabled(true);

        currencyService.getCurrencyMappings(key, new Callback<HashMap<String, String>>() {
            @Override
            public void success(HashMap<String, String> responseMap, Response response) {
                currencyService.getRates(key, new Callback<RateResponse>() {
                    @Override
                    public void success(RateResponse rateResponse, Response response) {
                        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                        prefs.edit().putLong(TIMESTAMP_KEY, rateResponse.getTimestamp()).apply();
                        TreeMap<String, Double> ratesMap = rateResponse.getRates();

                        List<Currency> allCurrencies = new ArrayList<>();
                        for (Map.Entry<String, Double> entry : ratesMap.entrySet()) {
                            Currency currency = new Currency(entry.getKey(), entry.getValue());
                            allCurrencies.add(currency);
                        }

                        databaseHelper.addCurrencies(allCurrencies);
                        currencyTypesSpinner.setAdapter(mCurrencyAdapter);
                        convertMoneyBtn.setEnabled(true);
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
    public void onClick(View view) {
        if (view.getId() == convertMoneyBtn.getId()) {
            if (moneyInput.getText().toString().isEmpty()) {
                ActivityHelper.createAlertDialog(getParent(), "Amount can't be empty", "Please enter Amount");
            } else if (currencyTypesSpinner.getSelectedItemPosition() == 0) {
                ActivityHelper.createAlertDialog(getParent(), "Base currency can't be empty", "Choose a currency from which you wan to convert");
            } else if (currencyTypesSpinner.getSelectedItemPosition() == 0) {
                ActivityHelper.createAlertDialog(getParent(), "Convert currency can't be empty", "Choose a currency to which you want to convert");
            } else {
                Double amount = Double.parseDouble(moneyInput.getText().toString());
                presenter.convertInputMoneyToASpecificCurrency(amount, fromCurrency, toCurrency);
            }
        }
    }
}
