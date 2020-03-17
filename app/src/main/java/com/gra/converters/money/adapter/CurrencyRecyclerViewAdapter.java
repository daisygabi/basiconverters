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
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView codeTxt;
        public TextView rateTxt;
        public TextView convertedValueTxt;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
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

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CurrencyRecyclerViewAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Currency currency = this.currencies.get(position);
        System.out.println("Currency: " + currency);

        if(currency.getConvertedValue() > 0) {
            viewHolder.convertedValueTxt.setVisibility(View.VISIBLE);
            viewHolder.convertedValueTxt.setText(activity.getResources().getString(R.string.converted_value_label) + currency.getConvertedValue());
        }

        viewHolder.codeTxt.setText(String.valueOf(currency.getCode()));
        viewHolder.rateTxt.setText(String.valueOf(currency.getRate()));
    }

    @Override
    public int getItemCount() {
        return this.currencies.size();
    }

}