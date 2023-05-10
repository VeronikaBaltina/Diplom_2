package org.example.model;

import java.util.List;

public class Ingredients {
    private boolean success;
    private List<IngredientData> data;

    public Ingredients() {
    }

    public Ingredients(boolean success, List<IngredientData> data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<IngredientData> getData() {
        return data;
    }

    public void setData(List<IngredientData> data) {
        this.data = data;
    }
}

