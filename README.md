Smart Pantry Manager
An Android application built with Java, XML layouts, and SQLite to help users track pantry ingredients and find recipes they can prepare using only ingredients already available.

Main features
Add pantry ingredients with a name, quantity, unit, and optional expiry date.
View pantry items in a list.
Edit and delete saved ingredients.
Store pantry data locally using SQLite.
Seed 20 recipes and their required ingredients when the database is first created.
Suggest a recipe only when every required ingredient is present in the pantry in a sufficient quantity.
View a recipe's ingredient list and cooking instructions.
Enable or disable the expiry-alert setting.

Technology
Android Studio
Java
XML layouts
SQLite / SQLiteOpenHelper
AndroidX AppCompat
Android ListView and custom ArrayAdapter classes
Git and GitHub



Database design
The local SQLite database is named smart_pantry.db and contains these tables:

pantry_items: pantry ingredient name, quantity, unit, and optional expiry date.
recipes: recipe name and cooking instructions.
recipe_ingredients: ingredients and quantities required for each recipe.
settings: saved application preferences, including the expiry-alert setting.
The database helper creates the tables and seeds the recipe catalogue when SQLite first creates the database. Existing installations do not automatically receive new seed data just because the Java source has changed; database upgrades or clearing the app's data may be needed when the schema or seed process changes during development.

Strict recipe-matching rule
A recipe is suggested only when all its required ingredients are available in the pantry and the available amount for each ingredient is at least the required amount. A recipe with even one missing or insufficient ingredient must not be suggested. Ingredient names and units are normalised before comparison, and supported mass and volume units are converted to common base units.

This is a quantity-aware local matching rule; it does not require a shopping trip or use an online service.

Set up and run
Install Android Studio and open the SmartPantryManager project.
Confirm that the Android application module uses Java and that the Android SDK configure
Copy the Java classes into the matching package folders under app/src/main/java/.
Add the required XML layouts and values resources. Ensure each layout contains the view IDs referenced by its Activity.
Confirm the app-level Gradle dependencies include AndroidX AppCompat and the other libraries referenced by the project.
Sync Gradle, build the project, and run it on an emulator or Android device.
Add a pantry ingredient, close and reopen the app, and confirm that the ingredient remains saved.
Test editing and deleting an ingredient.
Test recipe suggestions with all required ingredients present, then remove or reduce one required ingredient and confirm that the recipe is no longer suggested.




Author
Add your name, student number, module, and submission date here before submitting.
