package com.example.smartpantrymanager.models;

public class Recipe {
    private final int id;
    private final String name;
    private final String instructions;

    public Recipe(int id, String name, String instructions) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }
}
