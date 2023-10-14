package com.example.hikemate.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.HikeImage;

import java.util.List;

@Dao
public interface HikeImageDao {
    @Insert
    long insertImage(HikeImage hikeImage);

    @Update
    void updateImage(HikeImage hikeImage);

    @Query("SELECT * FROM hike_image")
    List<HikeImage> getAllHikeImage();

    @Query("SELECT * FROM hike_image WHERE hike_id =:id")
    HikeImage getHikeImageById(int id);

    @Delete
    void deleteHikeImage(HikeImage hikeImage);

}
