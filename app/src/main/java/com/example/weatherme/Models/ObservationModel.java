package com.example.weatherme.Models;
import androidx.annotation.NonNull;
import java.util.Locale;

public class ObservationModel {

    //properties
    private String title;
    private String content;
    private int UID;//UNIQUE ID

    //default constructor
    public ObservationModel(){}

    //constructor
    public ObservationModel(String title, String content, int uid) {
        this.title = title;
        this.content = content;
        this.UID = uid;
    }

    //Getters and setters
    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
