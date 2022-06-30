package com.example.thoughtfull2.models;

public class GratitudeModel {
    private String description;
    private int ID;

    public GratitudeModel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
