package com.example.yumyum.Model;

public class category {

    private String name;
    private String foto;


    public category(){

    }
    public category(String name, String foto) {
        this.name = name;
        this.foto= foto;
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
}
