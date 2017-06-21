package com.ufkoku.demo_app.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class AwesomeEntity implements Serializable, Parcelable {

    public static final Creator<AwesomeEntity> CREATOR = new Creator<AwesomeEntity>() {
        @Override
        public AwesomeEntity createFromParcel(Parcel in) {
            return new AwesomeEntity(in);
        }

        @Override
        public AwesomeEntity[] newArray(int size) {
            return new AwesomeEntity[size];
        }
    };

    private int importantDataField;

    public AwesomeEntity(int importantDataField) {
        this.importantDataField = importantDataField;
    }

    protected AwesomeEntity(Parcel in) {
        importantDataField = in.readInt();
    }

    public int getImportantDataField() {
        return importantDataField;
    }

    public void setImportantDataField(int importantDataField) {
        this.importantDataField = importantDataField;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(importantDataField);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
