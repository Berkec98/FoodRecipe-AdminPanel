package com.example.yumyum.Model;

public class Food {
    private String foodname;
    private String foodmaterials;
    private String foodrecipes;
    private String foodrecipeslink;
    private String SpeciesID;
    private String foto;

    public Food(){

    }

    public Food(String foodname, String foodmaterials, String foodrecipes, String foodrecipeslink, String foto, String speciesID) {
        this.foodname = foodname;
        this.foodmaterials = foodmaterials;
        this.foodrecipes = foodrecipes;
        this.foodrecipeslink = foodrecipeslink;
        this.foto=foto;
        this.SpeciesID = speciesID;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFoodmaterials() {
        return foodmaterials;
    }

    public void setFoodmaterials(String foodmaterials) {
        this.foodmaterials = foodmaterials;
    }

    public String getFoodrecipes() {
        return foodrecipes;
    }

    public void setFoodrecipes(String foodrecipes) {
        this.foodrecipes = foodrecipes;
    }

    public String getFoodrecipeslink() {
        return foodrecipeslink;
    }

    public void setFoodrecipeslink(String foodrecipeslink) {
        this.foodrecipeslink = foodrecipeslink;
    }

    public String getSpeciesID() {
        return SpeciesID;
    }

    public void setSpeciesID(String speciesID) {
        SpeciesID = speciesID;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
