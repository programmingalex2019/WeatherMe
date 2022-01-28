package com.example.weatherme.Models;

import androidx.annotation.NonNull;

import java.util.Locale;

public class ObservationModel {

    private String title;
    private String content;
    private int UID;

    public ObservationModel(){}

    public int getUID() {
        return UID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public ObservationModel(String title, String content, int uid) {
        this.title = title;
        this.content = content;
        this.UID = uid;
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

    @NonNull
    @Override
    public String toString() {
        String output = String.format(Locale.getDefault(),"%d %s %s", this.UID, this.title, this.content);
        return output;
    }
}
