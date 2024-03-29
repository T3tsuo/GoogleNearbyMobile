package com.example.googlenearbymobile.LocationSharingLibJava.src;

import java.util.ArrayList;

public class People {

    private String name;
    private Point currentLocation;

    public People(String name, Double longitude, Double latitude) {
        this.name = name;
        this.currentLocation = new Point(longitude, latitude);
    }

    public People(String name) {
        this.name = name;
        this.currentLocation = null;
    }

    public String getName() {
        return name;
    }

    public Point getCurrentLocation() {
        return currentLocation;
    }

    public String toString() {
        return getName() + "    " + currentLocation;
    }
}
