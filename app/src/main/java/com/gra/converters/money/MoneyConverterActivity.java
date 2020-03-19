package com.gra.converters.money;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gra.converters.R;
import com.gra.converters.money.adapter.CurrencyAdapter;
import com.gra.converters.money.adapter.CurrencyRecyclerViewAdapter;
import com.gra.converters.money.database.DatabaseHelper;
import com.gra.converters.money.model.Currency;
import com.gra.converters.utils.ActivityHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.gra.converters.utils.Constants.TIMESTAMP_KEY;

public class MoneyConverterActivity extends AppCompatActivity implements MoneyConverterContract.View, View.OnClickListener {

    @BindView(R.id.currencyTypes)
    Spinner currencyTypesSpinner;
    @BindView(R.id.moneyInput)
    EditText moneyInput;
    @BindView(R.id.convertMoneyBtn)
    Button convertMoneyBtn;
    @BindView(R.id.currenciesRecyclerView)
    RecyclerView currenciesRecyclerView;
    @BindView(R.id.searchInListTxt)
    EditText searchInListTxt;

    private MoneyConverterPresenter presenter;
    private CurrencyRecyclerViewAdapter recyclerViewAdapter;
    private Currency fromCurrency;

    private DatabaseHelper databaseHelper;
    private CurrencyAdapter mCurrencyAdapter;
    private String key;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.money_converter_activity);
        ButterKnife.bind(this);

        key = getString(R.string.key);
        presenter = new MoneyConverterPresenter(this);

        presenter.initRetrofit();
        initDefaultCurrencySpinnerAdapter();
        onSelectFromCurrencySpinner();
        convertMoneyBtn.setOnClickListener(this);
        initRecyclerView();
    }

    private void initRecyclerView() {
        List<Currency> existingCurrencies = databaseHelper.getCurrencies();
        recyclerViewAdapter = new CurrencyRecyclerViewAdapter(existingCurrencies, this);
        currenciesRecyclerView.setAdapter(recyclerViewAdapter);
        currenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void initDefaultCurrencySpinnerAdapter() {
        databaseHelper = DatabaseHelper.getInstance(this);
        if (databaseHelper.isDatabaseEmpty()) {
            downloadInformationIfNetworkIsAvailable();
        } else {
            mCurrencyAdapter = new CurrencyAdapter(this);
            currencyTypesSpinner.setAdapter(mCurrencyAdapter);
        }
    }

    private void onSelectFromCurrencySpinner() {
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
    public void updateCurrencyListDetails(List<Currency> currencies) {
        databaseHelper.deleteAllCurrencies(databaseHelper.getWritableDatabase());
        databaseHelper.addCurrencies(currencies);

        recyclerViewAdapter = new CurrencyRecyclerViewAdapter(currencies, this);
        currenciesRecyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        searchInListTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateCurrencySpinner(List<Currency> currencies) {
        databaseHelper.addCurrencies(currencies);

        mCurrencyAdapter = new CurrencyAdapter(this);
        currencyTypesSpinner.setAdapter(mCurrencyAdapter);
        mCurrencyAdapter.notifyDataSetChanged();
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
            ActivityHelper.createAlertDialog(this, getString(R.string.title_error_no_network), getString(R.string.message_error_no_network));
        } else {
            getCurrenciesFromService();
        }
    }

    public void getCurrenciesFromService() {
        presenter.getCurrencyMappings(getParent(), key);
    }

    @Override
    public void onClick(View view) {
        if (ActivityHelper.isNetworkAvailable(this) && view.getId() == convertMoneyBtn.getId()) {
            if (currencyTypesSpinner.getSelectedItemPosition() == 0) {
                ActivityHelper.createAlertDialog(this, getString(R.string.missing_information), getString(R.string.select_base_label));
            } else {
                boolean validInput = presenter.validateInput(moneyInput.getText().toString());
                if (validInput) {
                    double amount = Double.parseDouble(moneyInput.getText().toString());
                    presenter.convertInputMoneyToAllCurrencies(amount, databaseHelper.getCurrencies(), fromCurrency);
                }
            }
        } else {
            ActivityHelper.createAlertDialog(this, getString(R.string.title_error_no_network), getString(R.string.message_error_no_network));
        }
    }
}
