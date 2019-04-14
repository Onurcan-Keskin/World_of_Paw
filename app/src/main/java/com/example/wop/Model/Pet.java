package com.example.wop.Model;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public class Pet {
    private String mImageUrl;
    private String mName;
    private String mBreed;
    private int mAge;
    private String mOwner;
    private String mAddress;

    public Pet(String trim, Task<Uri> downloadUrl){}

    public Pet(String mImageUrl, String mName, String mOwner, String mBreed, int mAge,  String mAddress) {
        if (mName.trim().equals("")) mName="No name";
        this.mImageUrl = mImageUrl;
        this.mName = mName;
        this.mBreed = mBreed;
        this.mAge = mAge;
        this.mOwner = mOwner;
        this.mAddress = mAddress;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmBreed() {
        return mBreed;
    }

    public void setmBreed(String mBreed) {
        this.mBreed = mBreed;
    }

    public int getmAge() {
        return mAge;
    }

    public void setmAge(int mAge) {
        this.mAge = mAge;
    }

    public String getmOwner() {
        return mOwner;
    }

    public void setmOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }
}
