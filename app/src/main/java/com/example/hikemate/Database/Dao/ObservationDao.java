package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hikemate.Database.Model.Observation;

import java.util.List;

@Dao
public interface ObservationDao {
    @Insert
    long insert(Observation observation);

    @Update
    void update(Observation observation);

    @Query("SELECT * FROM observation")
    List<Observation> getAllObservation();

    @Delete
    void deleteObservation(Observation observation);

    @Query("SELECT * FROM observation WHERE name LIKE :searchQuery")
    List<Observation> searchObservation(String searchQuery);
}
