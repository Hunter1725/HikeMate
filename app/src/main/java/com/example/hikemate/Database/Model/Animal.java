package com.example.hikemate.Database.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "animal")
public class Animal implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imageUrl;
    private String nameVi;
    private String nameEn;
    private String danger;
    private String respondVi;
    private String respondEn;
    private String descriptionVi;
    private String descriptionEn;

    @Ignore
    public Animal() {
    }

    public Animal(String imageUrl, String nameVi, String nameEn, String danger, String respondVi, String respondEn, String descriptionVi, String descriptionEn) {
        this.imageUrl = imageUrl;
        this.nameVi = nameVi;
        this.nameEn = nameEn;
        this.danger = danger;
        this.respondVi = respondVi;
        this.respondEn = respondEn;
        this.descriptionVi = descriptionVi;
        this.descriptionEn = descriptionEn;
    }

    @Ignore
    protected Animal(Parcel in) {
        id = in.readInt();
        imageUrl = in.readString();
        nameVi = in.readString();
        nameEn = in.readString();
        danger = in.readString();
        respondVi = in.readString();
        respondEn = in.readString();
        descriptionVi = in.readString();
        descriptionEn = in.readString();
    }

    @Ignore
    public static final Creator<Animal> CREATOR = new Creator<Animal>() {
        @Override
        public Animal createFromParcel(Parcel in) {
            return new Animal(in);
        }

        @Override
        public Animal[] newArray(int size) {
            return new Animal[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNameVi() {
        return nameVi;
    }

    public void setNameVi(String nameVi) {
        this.nameVi = nameVi;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getDanger() {
        return danger;
    }

    public void setDanger(String danger) {
        this.danger = danger;
    }

    public String getRespondVi() {
        return respondVi;
    }

    public void setRespondVi(String respondVi) {
        this.respondVi = respondVi;
    }

    public String getRespondEn() {
        return respondEn;
    }

    public void setRespondEn(String respondEn) {
        this.respondEn = respondEn;
    }

    public String getDescriptionVi() {
        return descriptionVi;
    }

    public void setDescriptionVi(String descriptionVi) {
        this.descriptionVi = descriptionVi;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
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
        parcel.writeString(imageUrl);
        parcel.writeString(nameVi);
        parcel.writeString(nameEn);
        parcel.writeString(danger);
        parcel.writeString(respondVi);
        parcel.writeString(respondEn);
        parcel.writeString(descriptionVi);
        parcel.writeString(descriptionEn);
    }
}
