package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hikemate.Database.Model.Animal;
import com.example.hikemate.Database.Model.Setting;

import java.util.ArrayList;
import java.util.List;
@Dao
public interface AnimalDao {
    @Insert
    long insert(Animal animal);

    @Insert
    void insertAll(ArrayList<Animal> animal);

    @Update
    void update(Animal animal);

    @Query("SELECT * FROM animal")
    List<Animal> getAnimal();

    @Query("SELECT * FROM animal WHERE nameEn LIKE :name OR nameVi LIKE :name OR descriptionEn LIKE :name OR descriptionVi LIKE :name")
    List<Animal> searchAnimal(String name);
}
