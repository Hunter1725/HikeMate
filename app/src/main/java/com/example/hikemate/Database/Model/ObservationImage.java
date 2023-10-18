package com.example.hikemate.Database.Model;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "observation_image",
        foreignKeys = {@ForeignKey(entity = Hike.class, parentColumns = "id", childColumns = "observation_id", onDelete = ForeignKey.CASCADE)})
public class ObservationImage {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private Bitmap data;
    @ColumnInfo(name = "observation_id")
    private int observationId;

    public ObservationImage(Bitmap data, int observationId) {
        this.data = data;
        this.observationId = observationId;
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

    public int getObservationId() {
        return observationId;
    }

    public void setObservationId(int observationId) {
        this.observationId = observationId;
    }
}
