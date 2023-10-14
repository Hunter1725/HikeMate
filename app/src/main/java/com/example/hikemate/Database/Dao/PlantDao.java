package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hikemate.Database.Model.Animal;
import com.example.hikemate.Database.Model.Plant;
import com.example.hikemate.Database.Model.Setting;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface PlantDao {
    @Insert
    long insert(Plant plant);

    @Update
    void update(Plant plant);

    @Insert
    void insertAll(ArrayList<Plant> plant);

    @Query("SELECT * FROM plant")
    List<Plant> getPlan();

    @Query("SELECT * FROM plant WHERE nameEn LIKE :name OR nameVi LIKE :name OR descriptionEn LIKE :name OR descriptionVi LIKE :name")
    List<Plant> searchPlant(String name);
}
