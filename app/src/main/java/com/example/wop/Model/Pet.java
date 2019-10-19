package com.example.wop.Model;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Pet {

    private String pet_image;
    private String pet_thumb_image;
    private String pet_name;
    private String pet_breed;
    private String pet_age;
    private String pet_address;
    private String pet_ble_location;
    private String pet_nfc_location;

    public Pet() {
    }

    public Pet(String pet_image, String pet_thumb_image, String pet_name, String pet_breed, String pet_age, String pet_address, String pet_ble_location, String pet_nfc_location) {
        this.pet_image = pet_image;
        this.pet_thumb_image = pet_thumb_image;
        this.pet_name = pet_name;
        this.pet_breed = pet_breed;
        this.pet_age = pet_age;
        this.pet_address = pet_address;
        this.pet_ble_location = pet_ble_location;
        this.pet_nfc_location = pet_nfc_location;
    }

    public String getPet_ble_location() {
        return pet_ble_location;
    }

    public void setPet_ble_location(String pet_ble_location) {
        this.pet_ble_location = pet_ble_location;
    }

    public String getPet_nfc_location() {
        return pet_nfc_location;
    }

    public void setPet_nfc_location(String pet_nfc_location) {
        this.pet_nfc_location = pet_nfc_location;
    }

    public String getPet_thumb_image() {
        return pet_thumb_image;
    }

    public void setPet_thumb_image(String pet_thumb_image) {
        this.pet_thumb_image = pet_thumb_image;
    }

    public String getPet_image() {
        return pet_image;
    }

    public void setPet_image(String pet_image) {
        this.pet_image = pet_image;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public String getPet_breed() {
        return pet_breed;
    }

    public void setPet_breed(String pet_breed) {
        this.pet_breed = pet_breed;
    }

    public String getPet_age() {
        return pet_age;
    }

    public void setPet_age(String pet_age) {
        this.pet_age = pet_age;
    }

    public String getPet_address() {
        return pet_address;
    }

    public void setPet_address(String pet_address) {
        this.pet_address = pet_address;
    }

    public Pet(String trim, Task<Uri> downloadUrl){}

}
