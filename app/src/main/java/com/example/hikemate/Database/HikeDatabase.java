package com.example.hikemate.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import com.example.hikemate.Database.Dao.MessageDao;
import com.example.hikemate.Database.Dao.SettingDao;
import com.example.hikemate.Database.Dao.WeatherDao;
import com.example.hikemate.Database.Model.Message;
import com.example.hikemate.Database.Model.Setting;
import com.example.hikemate.Database.Model.Weather;

@Database(entities = {Setting.class, Weather.class, Message.class}, version = 1)
public abstract class HikeDatabase extends androidx.room.RoomDatabase {
    public abstract SettingDao settingDao();
    public abstract WeatherDao weatherDao();
    public abstract MessageDao messageDao();
    private static HikeDatabase instance;

    public static synchronized HikeDatabase getInstance(Context context) {
        if (instance == null){
            instance = Room.databaseBuilder(context, HikeDatabase.class, "room_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
