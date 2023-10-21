package com.example.hikemate.Database.Model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

@Entity(tableName = "hike_image",
        foreignKeys = {@ForeignKey(entity = Hike.class, parentColumns = "id", childColumns = "hike_id", onDelete = ForeignKey.CASCADE)})
public class HikeImage implements Parcelable {

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

    @Ignore
    public HikeImage(Bitmap data) {
        this.data = data;
    }

    @Ignore
    protected HikeImage(Parcel in) {
        id = in.readInt();
        data = in.readParcelable(Bitmap.class.getClassLoader());
        hikeId = in.readInt();
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(data, flags);
        dest.writeInt(hikeId);
    }

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    public static final Creator<HikeImage> CREATOR = new Creator<HikeImage>() {
        @Override
        public HikeImage createFromParcel(Parcel in) {
            return new HikeImage(in);
        }

        @Override
        public HikeImage[] newArray(int size) {
            return new HikeImage[size];
        }
    };

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
