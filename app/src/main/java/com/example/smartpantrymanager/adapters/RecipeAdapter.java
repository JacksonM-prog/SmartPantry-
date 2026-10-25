package com.example.smartpantrymanager.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.example.smartpantrymanager.models.Recipe;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    public RecipeAdapter(Activity context, List<Recipe> recipes) {
        super(context, android.R.layout.simple_list_item_2, recipes);
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

        Recipe recipe = getItem(position);

        TextView name = view.getText1();
        TextView details = view.getText2();

        if (recipe != null) {
            name.setText(recipe.getName());
            details.setText("Tap to view ingredients and method");
        }

        return view;
    }
}
