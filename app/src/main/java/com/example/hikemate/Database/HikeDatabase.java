package com.example.hikemate.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;

import com.example.hikemate.Database.Dao.SettingDao;
import com.example.hikemate.Database.Model.Setting;

@Database(entities = {Setting.class}, version = 1)
public abstract class HikeDatabase extends androidx.room.RoomDatabase {
    public abstract SettingDao settingDao();
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
