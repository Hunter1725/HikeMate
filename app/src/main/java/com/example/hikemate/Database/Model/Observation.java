package com.example.hikemate.Database.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "observation",
        foreignKeys = {@ForeignKey(entity = Hike.class, parentColumns = "id", childColumns = "hike_id", onDelete = ForeignKey.CASCADE)})
public class Observation implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "hike_id")
    private int hikeId;
    private String name;
    private Long timeObservation;
    private String additionalComment;

    @Ignore
    public Observation(int hikeId, String name, Long timeObservation) {
        this.hikeId = hikeId;
        this.name = name;
        this.timeObservation = timeObservation;
    }

    public Observation(int hikeId, String name, Long timeObservation, String additionalComment) {
        this.hikeId = hikeId;
        this.name = name;
        this.timeObservation = timeObservation;
        this.additionalComment = additionalComment;
    }

    @Ignore
    public Observation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHikeId() {
        return hikeId;
    }

    public void setHikeId(int hikeId) {
        this.hikeId = hikeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimeObservation() {
        return timeObservation;
    }

    public void setTimeObservation(Long timeObservation) {
        this.timeObservation = timeObservation;
    }

    public String getAdditionalComment() {
        return additionalComment;
    }

    public void setAdditionalComment(String additionalComment) {
        this.additionalComment = additionalComment;
    }

    @Ignore
    protected Observation(Parcel in) {
        id = in.readInt();
        hikeId = in.readInt();
        name = in.readString();
        if (in.readByte() == 0) {
            timeObservation = null;
        } else {
            timeObservation = in.readLong();
        }
        additionalComment = in.readString();
    }
    @Ignore
    public static final Creator<Observation> CREATOR = new Creator<Observation>() {
        @Override
        public Observation createFromParcel(Parcel in) {
            return new Observation(in);
        }

        @Override
        public Observation[] newArray(int size) {
            return new Observation[size];
        }
    };

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(hikeId);
        parcel.writeString(name);
        if (timeObservation == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(timeObservation);
        }
        parcel.writeString(additionalComment);
    }
}
