package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.adapters.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.database.RecipeMatcher;
import com.example.smartpantrymanager.models.Recipe;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper database;
    private ListView listRecipes;
    private TextView txtNoRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        database = new DatabaseHelper(getApplicationContext());

        listRecipes = findViewById(R.id.listRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);

        Button back = findViewById(R.id.btnBackToPantry);
        back.setOnClickListener(v -> finish());

        listRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {
                    Recipe recipe =
                            (Recipe) parent.getItemAtPosition(position);

                    Intent intent = new Intent(
                            this, RecipeDetailActivity.class);

                    intent.putExtra("recipe_id", recipe.getId());
                    startActivity(intent);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestions();
    }

    private void loadSuggestions() {
        List<Recipe> recipes =
                new RecipeMatcher(database).getSuggestedRecipes();

        listRecipes.setAdapter(
                new RecipeAdapter(this, recipes));

        boolean empty = recipes.isEmpty();

        listRecipes.setVisibility(
                empty ? View.GONE : View.VISIBLE);

        txtNoRecipes.setVisibility(
                empty ? View.VISIBLE : View.GONE);
    }
}
