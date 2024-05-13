package com.example.googlenearbymobile;

public class PeopleLocation {

    private final String name;
    private final String place;
    private final double longitude;
    private final double latitude;
    private boolean atLocation;

    public PeopleLocation(String name, String place, double latitude, double longitude) {
        this.name = name;
        this.place = place;
        this.latitude = latitude;
        this.longitude = longitude;
        this.atLocation = false;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public boolean isAtLocation() {
        return atLocation;
    }

    public void setAtLocation(boolean value) {
        this.atLocation = value;
    }

    public String getPlace() {
        return this.place;
    }
}
