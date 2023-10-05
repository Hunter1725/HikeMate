package com.example.hikemate.Database.Model;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "setting")
public class Setting {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String language;
    private boolean notification;

    public Setting(String language, boolean notification) {
        this.language = language;
        this.notification = notification;
    }

    public Setting() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }
}
