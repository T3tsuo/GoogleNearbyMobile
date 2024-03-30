package com.example.googlenearbymobile.LocationSharingLibJava.src;

import androidx.annotation.NonNull;

public class Point {
    private final double x;
    private final double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    @NonNull
    public String toString(){
        return "("+ this.x + ","+ this.y + ")";
    }

    public double getLongitude() {
        return x;
    }
    public double getLatitude() {
        return y;
    }
}