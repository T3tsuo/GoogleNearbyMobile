package com.example.googlenearbymobile;

public class PeopleLocation {

    private String name;
    private String place;
    private double longitude;
    private double latitude;
    private boolean atLocation;

    public PeopleLocation(String name, String place, double longitude, double latitude) {
        this.name = name;
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
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
