package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hikemate.Database.Model.Setting;

@Dao
public interface SettingDao {
    @Insert
    long insert(Setting setting);

    @Update
    void update(Setting setting);

    @Query("SELECT * FROM setting")
    Setting getSetting();
}
