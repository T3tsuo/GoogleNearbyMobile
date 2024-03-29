package com.example.googlenearbymobile;


import java.sql.Timestamp;
import java.util.Date;

import com.example.googlenearbymobile.LocationSharingLibJava.src.People;

public class PeopleNearby {
    Timestamp timeStamp;
    boolean near;
    People person;

    public PeopleNearby(People person, Timestamp timeStamp, boolean near) {
        this.person = person;
        this.timeStamp = timeStamp;
        this.near = near;
    }

    public boolean canRefresh(boolean isNear) {
        return this.timeStamp.before(new Date()) && this.near != isNear;
    }

    public boolean isPersonInList(String name) {
        return name.equals(person.getName());
    }

    public void setTimeStamp(Timestamp timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setNear(boolean near) {
        this.near = near;
    }
}
