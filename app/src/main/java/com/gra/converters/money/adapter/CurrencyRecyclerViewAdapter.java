package com.gra.converters.money.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gra.converters.R;
import com.gra.converters.money.model.Currency;

import java.util.ArrayList;
import java.util.List;

public class CurrencyRecyclerViewAdapter extends RecyclerView.Adapter<CurrencyRecyclerViewAdapter.ViewHolder> {

    private List<Currency> currencies = new ArrayList<>();
    private Activity activity;

    public CurrencyRecyclerViewAdapter(List<Currency> currencies, Activity activity) {
        this.currencies = currencies;
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView codeTxt;
        public TextView rateTxt;
        public TextView convertedValueTxt;

        public ViewHolder(View itemView) {
            super(itemView);

            codeTxt = (TextView) itemView.findViewById(R.id.codeTxt);
            rateTxt = (TextView) itemView.findViewById(R.id.rateTxt);
            convertedValueTxt = itemView.findViewById(R.id.convertedValueTxt);
        }
    }

    @Override
    public CurrencyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.currency_recyclerview_item, parent, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(CurrencyRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Currency currency = this.currencies.get(position);
        if (currency.getConvertedValue() > 0) {
            viewHolder.convertedValueTxt.setVisibility(View.VISIBLE);
            viewHolder.convertedValueTxt.setText(activity.getResources().getString(R.string.converted_value_label) + " " + currency.getConvertedValue());
        }

        viewHolder.codeTxt.setText(String.valueOf(currency.getCode()));
        viewHolder.rateTxt.setText(String.valueOf(currency.getRate()));
    }

    @Override
    public int getItemCount() {
        return this.currencies.size();
    }

}