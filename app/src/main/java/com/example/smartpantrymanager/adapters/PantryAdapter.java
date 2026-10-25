
package com.example.smartpantrymanager.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;
import android.view.LayoutInflater;

import com.example.smartpantrymanager.models.PantryItem;

import java.util.List;
import java.util.Locale;

public class PantryAdapter extends ArrayAdapter<PantryItem> {

    public PantryAdapter(Activity context, List<PantryItem> items) {
        super(context, android.R.layout.simple_list_item_2, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TwoLineListItem view;

        if (convertView == null) {
            view = (TwoLineListItem) LayoutInflater.from(getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
        } else {
            view = (TwoLineListItem) convertView;
        }

        PantryItem item = getItem(position);

        TextView title = view.getText1();
        TextView detail = view.getText2();

        if (item != null) {
            title.setText(item.getName());
            title.setTypeface(null, Typeface.BOLD);

            detail.setText(String.format(
                    Locale.getDefault(),
                    "Quantity: %s %s  |  Expiry: %s",
                    item.getQuantity(),
                    item.getUnit(),
                    item.getExpiryDate() == null
                            || item.getExpiryDate().isEmpty()
                            ? "Not set"
                            : item.getExpiryDate()
            ));
        }

        return view;
    }
}
