package com.example.hikemate.Database.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.hikemate.Database.Model.Hike;

@Entity(tableName = "hike")
public class Hike implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String hikeName;
    private String location;
    private Long date;
    private boolean parkingAvailable;
    private double length;
    private int difficulty;
    private String description;


    public Hike(String hikeName, String location, Long date, boolean parkingAvailable, double length, int difficulty, String description) {
        this.hikeName = hikeName;
        this.location = location;
        this.date = date;
        this.parkingAvailable = parkingAvailable;
        this.length = length;
        this.difficulty = difficulty;
        this.description = description;
    }

    @Ignore
    protected Hike(Parcel in) {
        id = in.readInt();
        hikeName = in.readString();
        location = in.readString();
        if (in.readByte() == 0) {
            date = null;
        } else {
            date = in.readLong();
        }
        parkingAvailable = in.readByte() != 0;
        length = in.readDouble();
        difficulty = in.readInt();
        description = in.readString();
    }
    @Ignore
    public static final Creator<Hike> CREATOR = new Creator<Hike>() {
        @Override
        public Hike createFromParcel(Parcel in) {
            return new Hike(in);
        }

        @Override
        public Hike[] newArray(int size) {
            return new Hike[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHikeName() {
        return hikeName;
    }

    public void setHikeName(String hikeName) {
        this.hikeName = hikeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Hike{" +
                "id=" + id +
                ", hikeName='" + hikeName + '\'' +
                ", location='" + location + '\'' +
                ", date=" + date +
                ", parkingAvailable=" + parkingAvailable +
                ", length=" + length +
                ", difficulty=" + difficulty +
                ", description='" + description + '\'' +
                '}';
    }
    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }
    @Ignore
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(hikeName);
        parcel.writeString(location);
        if (date == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(date);
        }
        parcel.writeByte((byte) (parkingAvailable ? 1 : 0));
        parcel.writeDouble(length);
        parcel.writeInt(difficulty);
        parcel.writeString(description);
    }
}
