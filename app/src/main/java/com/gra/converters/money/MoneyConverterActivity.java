package com.gra.converters.money;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.gra.converters.utils.ActivityHelper;
import com.gra.converters.utils.ErrorDialogFragment;

import java.util.List;

import static com.gra.converters.utils.Constants.TIMESTAMP_KEY;

public class MoneyConverterActivity extends Activity implements MoneyConverterContract.View, View.OnClickListener {

    private Spinner currencyTypesSpinner;
    private EditText moneyInput;
    private Button convertMoneyBtn;
    private TextView resultTxt;
    private MoneyConverterPresenter presenter;
    private Currency fromCurrency;
    private Currency toCurrency = new Currency("RON", 4.2984);

    private DatabaseHelper databaseHelper;
    private CurrencyAdapter mCurrencyAdapter;
    private String key;


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

        presenter.initRetrofit();
        initAdapter();
        initSpinnerOnSelect();
        convertMoneyBtn.setOnClickListener(this);
    }

    private void initAdapter() {
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
    public void updateCurrencySpinner(List<Currency> currencies) {
        databaseHelper.addCurrencies(currencies);
        currencyTypesSpinner.setAdapter(mCurrencyAdapter);
    }

    @Override
    public void updateTimestampForWhenCurrenciesWereDownloadedLast(long timestamp) {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        prefs.edit().putLong(TIMESTAMP_KEY, timestamp).apply();
    }

    @Override
    public void showError() {
        Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_LONG).show();
    }

    @Override
    public void enableConvertButton() {
        convertMoneyBtn.setEnabled(true);
    }

    private void downloadInformationIfNetworkIsAvailable() {
        if (!ActivityHelper.isNetworkAvailable(this)) {
            ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(getString(R.string.title_error_no_network), getString(R.string.message_error_no_network));
            fragment.show(getFragmentManager(), "FRAGMENT_ERROR");
        } else {
            getCurrenciesFromService();
        }
    }

    public void getCurrenciesFromService() {
        presenter.getCurrencyMappings(getParent(), key);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == convertMoneyBtn.getId()) {
            if (currencyTypesSpinner.getSelectedItemPosition() == 0) {
                ActivityHelper.createAlertDialog(this, "Missing Information", "Please select the base currency");
            } else {
                boolean validInput = presenter.validateInput(moneyInput.getText().toString());
                if (validInput) {
                    double amount = Double.parseDouble(moneyInput.getText().toString());
                    presenter.convertInputMoneyToASpecificCurrency(amount, fromCurrency, toCurrency);
                }
            }
        }
    }
}
