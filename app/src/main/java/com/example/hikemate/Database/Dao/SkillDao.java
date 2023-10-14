package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hikemate.Database.Model.Animal;
import com.example.hikemate.Database.Model.Plant;
import com.example.hikemate.Database.Model.Setting;
import com.example.hikemate.Database.Model.Skill;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface SkillDao {
    @Insert
    long insert(Skill skill);

    @Update
    void update(Skill skill);

    @Insert
    void insertAll(ArrayList<Skill> skill);

    @Query("SELECT * FROM skill")
    List<Skill> getSkill();

    @Query("SELECT * FROM skill WHERE nameEn LIKE :name OR nameVi LIKE :name OR descriptionEn LIKE :name OR descriptionVi LIKE :name")
    List<Skill> searchSkill(String name);

}
