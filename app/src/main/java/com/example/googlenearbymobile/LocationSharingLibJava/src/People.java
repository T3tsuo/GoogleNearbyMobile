package com.example.googlenearbymobile.LocationSharingLibJava.src;


import androidx.annotation.NonNull;

public class People {

    private final String name;
    private final double latitude;
    private final double longitude;

    public People(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() { return longitude; }

    @NonNull
    public String toString() {
        return getName() + ", (" + latitude + ", " + longitude + ")";
    }
}
