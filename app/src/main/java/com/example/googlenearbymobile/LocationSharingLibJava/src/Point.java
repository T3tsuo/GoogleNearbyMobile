package com.example.googlenearbymobile.LocationSharingLibJava.src;

public class Point {
    private double x;
    private double y;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }
    
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