package com.example.googlenearbymobile.LocationSharingLibJava.src;


import androidx.annotation.NonNull;

public class People {

    private final String name;
    private final Point currentLocation;

    public People(String name, Double longitude, Double latitude) {
        this.name = name;
        this.currentLocation = new Point(longitude, latitude);
    }

    public String getName() {
        return name;
    }

    public Point getCurrentLocation() {
        return currentLocation;
    }

    @NonNull
    public String toString() {
        return getName() + "    " + currentLocation;
    }
}
