package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.models.PantryItem;
import com.example.smartpantrymanager.models.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry_items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "quantity REAL NOT NULL CHECK(quantity > 0)," +
                "unit TEXT NOT NULL," +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL UNIQUE," +
                "instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "recipe_id INTEGER NOT NULL," +
                "ingredient_name TEXT NOT NULL," +
                "required_quantity REAL NOT NULL," +
                "unit TEXT NOT NULL," +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");

        db.execSQL("CREATE TABLE settings (" +
                "setting_key TEXT PRIMARY KEY," +
                "setting_value TEXT NOT NULL)");

        db.execSQL("INSERT INTO settings VALUES ('expiry_alerts', 'true')");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        // Add database migrations here when the schema changes.
    }

    // CREATE: Add a pantry ingredient.
    public long addPantryItem(String name, double quantity,
                              String unit, String expiryDate) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", normalizeName(name));
        values.put("quantity", quantity);
        values.put("unit", normalizeUnit(unit));
        values.put("expiry_date", expiryDate);

        return db.insert("pantry_items", null, values);
    }

    // READ: Retrieve all pantry ingredients.
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        try (Cursor cursor = db.query(
                "pantry_items", null, null, null,
                null, null, "name ASC")) {

            while (cursor.moveToNext()) {
                items.add(new PantryItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"))
                ));
            }
        }

        return items;
    }

    // UPDATE: Change an existing pantry ingredient.
    public int updatePantryItem(int id, String name, double quantity,
                                String unit, String expiryDate) {
        ContentValues values = new ContentValues();
        values.put("name", normalizeName(name));
        values.put("quantity", quantity);
        values.put("unit", normalizeUnit(unit));
        values.put("expiry_date", expiryDate);

        return getWritableDatabase().update(
                "pantry_items", values, "id = ?",
                new String[]{String.valueOf(id)});
    }

    // DELETE: Remove an ingredient.
    public int deletePantryItem(int id) {
        return getWritableDatabase().delete(
                "pantry_items", "id = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().query(
                "recipes", null, null, null,
                null, null, "name ASC")) {

            while (cursor.moveToNext()) {
                recipes.add(new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                ));
            }
        }

        return recipes;
    }

    public Recipe getRecipe(int recipeId) {
        try (Cursor cursor = getReadableDatabase().query(
                "recipes", null, "id = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, null)) {

            if (cursor.moveToFirst()) {
                return new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("instructions"))
                );
            }
        }

        return null;
    }

    public static class RequiredIngredient {
        public final String name;
        public final double quantity;
        public final String unit;

        public RequiredIngredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    public List<RequiredIngredient> getRequiredIngredients(int recipeId) {
        List<RequiredIngredient> ingredients = new ArrayList<>();

        try (Cursor cursor = getReadableDatabase().query(
                "recipe_ingredients", null, "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null, null, "ingredient_name ASC")) {

            while (cursor.moveToNext()) {
                ingredients.add(new RequiredIngredient(
                        cursor.getString(cursor.getColumnIndexOrThrow(
                                "ingredient_name")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(
                                "required_quantity")),
                        cursor.getString(cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        }

        return ingredients;
    }

    public boolean getExpiryAlertsEnabled() {
        try (Cursor cursor = getReadableDatabase().query(
                "settings", new String[]{"setting_value"},
                "setting_key = ?", new String[]{"expiry_alerts"},
                null, null, null)) {

            return cursor.moveToFirst()
                    && Boolean.parseBoolean(cursor.getString(0));
        }
    }

    public void setExpiryAlertsEnabled(boolean enabled) {
        ContentValues values = new ContentValues();
        values.put("setting_value", String.valueOf(enabled));

        getWritableDatabase().update(
                "settings", values, "setting_key = ?",
                new String[]{"expiry_alerts"});
    }

    public static String normalizeName(String name) {
        String result = name.trim().toLowerCase(Locale.ROOT);

        if (result.endsWith("ies") && result.length() > 4) {
            result = result.substring(0, result.length() - 3) + "y";
        } else if (result.endsWith("oes") && result.length() > 4) {
            result = result.substring(0, result.length() - 2);
        } else if (result.endsWith("s")
                && !result.endsWith("ss")
                && result.length() > 3) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    public static String normalizeUnit(String unit) {
        String result = unit.trim().toLowerCase(Locale.ROOT);

        switch (result) {
            case "pieces":
            case "piece":
            case "pcs":
            case "pc":
                return "piece";
            case "grams":
            case "gram":
                return "g";
            case "kilograms":
            case "kilogram":
                return "kg";
            case "millilitres":
            case "milliliters":
            case "millilitre":
            case "milliliter":
                return "ml";
            case "litres":
            case "liters":
            case "litre":
            case "liter":
                return "l";
            case "tablespoons":
                return "tbsp";
            case "teaspoons":
                return "tsp";
            default:
                return result;
        }
    }




    private void seedRecipes(SQLiteDatabase db) {
        String[][] data = {
                {"Chicken Fried Rice", "Cook chicken thoroughly, stir-fry with vegetables, add cooked rice and soy sauce.",
                        "chicken:150:g", "rice:200:g", "carrot:1:piece", "peas:50:g", "soy_sauce:15:ml", "oil:10:ml"},
                {"Beef and Potato Stew", "Brown beef, add diced potatoes and carrots with water, then simmer until tender.",
                        "beef:200:g", "potato:2:piece", "carrot:1:piece", "onion:1:piece", "oil:10:ml", "salt:2:g"},
                {"Tomato Egg Pasta", "Cook pasta, sauté tomato and garlic, add beaten eggs and combine with pasta.",
                        "pasta:150:g", "tomato:2:piece", "egg:2:piece", "garlic:1:piece", "oil:10:ml"},
                {"Chicken Curry", "Cook onion and curry powder, add chicken and tomatoes, then simmer until cooked.",
                        "chicken:200:g", "onion:1:piece", "tomato:2:piece", "curry_powder:5:g", "oil:10:ml", "salt:2:g"},
                {"Bean and Vegetable Soup", "Simmer beans with chopped carrots, potatoes, onions and stock until tender.",
                        "beans:150:g", "carrot:1:piece", "potato:1:piece", "onion:1:piece", "stock:500:ml"},
                {"Cheesy Baked Potatoes", "Bake potatoes until tender, split open and top with grated cheese and butter.",
                        "potato:2:piece", "cheese:50:g", "butter:10:g", "salt:1:g"},
                {"Chicken Mayo Sandwich", "Cook and shred chicken, mix with mayonnaise and serve between bread slices.",
                        "chicken:100:g", "mayonnaise:20:g", "bread:2:piece", "lettuce:20:g"},
                {"Beef Mince Spaghetti", "Brown beef mince with onion and tomato, simmer into a sauce and serve over cooked spaghetti.",
                        "beef_mince:200:g", "spaghetti:150:g", "tomato:2:piece", "onion:1:piece", "oil:10:ml"},
                {"Vegetable Stir Fry", "Stir-fry broccoli, carrots and bell pepper, then season and serve with rice.",
                        "broccoli:100:g", "carrot:1:piece", "bell_pepper:1:piece", "rice:150:g", "soy_sauce:15:ml", "oil:10:ml"},
                {"Tuna Pasta Salad", "Cook and cool pasta, then mix with tuna, mayonnaise and chopped cucumber.",
                        "pasta:150:g", "tuna:100:g", "mayonnaise:20:g", "cucumber:1:piece"},
                {"Chicken and Potato Roast", "Season chicken and potatoes with oil and herbs, then roast until cooked.",
                        "chicken:200:g", "potato:2:piece", "oil:15:ml", "mixed_herbs:2:g", "salt:2:g"},
                {"Lentil Curry", "Cook onion and curry powder, add lentils and tomatoes, then simmer until tender.",
                        "lentils:150:g", "tomato:2:piece", "onion:1:piece", "curry_powder:5:g", "oil:10:ml", "water:400:ml"},
                {"Cheese Omelette", "Beat eggs, pour into a pan and add grated cheese before folding.",
                        "egg:2:piece", "cheese:40:g", "butter:5:g", "salt:1:g"},
                {"Banana Oat Porridge", "Cook oats in milk until creamy, then top with sliced banana and honey.",
                        "oats:60:g", "milk:200:ml", "banana:1:piece", "honey:10:g"},
                {"Creamy Chicken Pasta", "Cook pasta, sauté chicken and garlic until cooked, then stir in cream.",
                        "chicken:150:g", "pasta:150:g", "cream:75:ml", "garlic:1:piece", "oil:10:ml"},
                {"Beef Tacos", "Cook beef mince with taco seasoning and serve in tortillas with tomato and lettuce.",
                        "beef_mince:200:g", "tortilla:3:piece", "tomato:1:piece", "lettuce:30:g", "taco_seasoning:5:g"},
                {"Sweet Potato Fries", "Cut sweet potatoes into strips, toss with oil and seasoning, then bake until crisp.",
                        "sweet_potato:2:piece", "oil:15:ml", "paprika:2:g", "salt:1:g"},
                {"Chickpea Salad", "Rinse cooked chickpeas, chop cucumber and tomato, then toss with lemon juice and olive oil.",
                        "chickpeas:150:g", "cucumber:1:piece", "tomato:1:piece", "lemon:1:piece", "olive_oil:10:ml"},
                {"Spinach and Chicken Rice", "Cook chicken thoroughly, add spinach and serve with cooked rice.",
                        "chicken:150:g", "spinach:60:g", "rice:200:g", "garlic:1:piece", "oil:10:ml"},
                {"Apple Cinnamon Oats", "Cook oats in milk, stir in chopped apple and cinnamon, then serve warm.",
                        "oats:60:g", "milk:200:ml", "apple:1:piece", "cinnamon:2:g", "honey:10:g"}
        };

        for (String[] row : data) {
            ContentValues recipe = new ContentValues();
            recipe.put("name", row[0]);
            recipe.put("instructions", row[1]);

            long recipeId = db.insert("recipes", null, recipe);

            if (recipeId == -1) {
                continue;
            }

            for (int i = 2; i < row.length; i++) {
                String[] parts = row[i].split(":");

                ContentValues ingredient = new ContentValues();
                ingredient.put("recipe_id", recipeId);
                ingredient.put("ingredient_name",
                        normalizeName(parts[0]));
                ingredient.put("required_quantity",
                        Double.parseDouble(parts[1]));
                ingredient.put("unit", normalizeUnit(parts[2]));

                db.insert("recipe_ingredients", null, ingredient);
            }
        }
    }


}

