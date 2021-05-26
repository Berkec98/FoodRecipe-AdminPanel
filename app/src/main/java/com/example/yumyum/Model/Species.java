package com.example.yumyum.Model;

public class Species {

    private String name;
    private String foto;
    private String categoryID;



    public Species(){

    }
    public Species(String name, String foto, String categoryID) {
        this.name= name;
        this.foto= foto;
        this.categoryID= categoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }
}
