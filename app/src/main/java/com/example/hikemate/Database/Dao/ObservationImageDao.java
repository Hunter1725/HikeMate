package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hikemate.Database.Model.ObservationImage;

import java.util.List;

@Dao
public interface ObservationImageDao {
    @Insert
    long insertImage(ObservationImage observationImage);

    @Update
    void updateImage(ObservationImage observationImage);

    @Query("SELECT * FROM observation_image")
    List<ObservationImage> getAllImage();

    @Query("SELECT * FROM observation_image WHERE observation_id =:id")
    ObservationImage getObservationImageById(int id);

    @Delete
    void deleteObservationImage(ObservationImage observationImage);
}
