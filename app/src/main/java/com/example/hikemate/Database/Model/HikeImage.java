package com.example.hikemate.Database.Model;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "hike_image",
        foreignKeys = {@ForeignKey(entity = Hike.class, parentColumns = "id", childColumns = "hike_id", onDelete = ForeignKey.CASCADE)})
public class HikeImage {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private Bitmap data;
    @ColumnInfo(name = "hike_id")
    private int hikeId;

    public HikeImage(Bitmap data, int hikeId) {
        this.data = data;
        this.hikeId = hikeId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getData() {
        return data;
    }

    public void setData(Bitmap data) {
        this.data = data;
    }

    public int getHikeId() {
        return hikeId;
    }

    public void setHikeId(int hikeId) {
        this.hikeId = hikeId;
    }
}
