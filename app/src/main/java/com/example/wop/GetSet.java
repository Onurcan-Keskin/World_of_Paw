package com.example.wop;

import android.graphics.Bitmap;

public class GetSet {
    String label;
    Bitmap image;

    public GetSet() {

    }


    String imageSrc;

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    public int getListItemPosition() {
        return listItemPosition;
    }

    public void setListItemPosition(int listItemPosition) {
        this.listItemPosition = listItemPosition;
    }

    int listItemPosition;

    public boolean isHaveImage() {
        return haveImage;
    }

    public void setHaveImage(boolean haveImage) {
        this.haveImage = haveImage;
    }

    boolean haveImage;

    public String getSubtext() {
        return subtext;
    }

    public void setSubtext(String subtext) {
        this.subtext = subtext;
    }

    String subtext;
    boolean status;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
