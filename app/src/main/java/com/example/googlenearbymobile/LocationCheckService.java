package com.example.googlenearbymobile;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.googlenearbymobile.LocationSharingLibJava.src.CookieReader;
import com.example.googlenearbymobile.LocationSharingLibJava.src.People;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class LocationCheckService extends Service {
    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    Handler handler = new Handler();
    String locationsData = "";
    Map<String,String> cookies = null;
    ArrayList<PeopleLocation> peoplesLocation;
    ArrayList<PeopleNearby> peoplesNearby;
    Integer NEARBY_WAIT_REFRESH = 3600;
    double NEARBY_DISTANCE_KM = 0.3;
    static double LOCATION_DISTANCE_KM = 0.15;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getNotification("Started.."));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationsData = intent.getStringExtra("Locations");
        assert locationsData != null;
        // save peoples location to check later
        peoplesLocation = createLocationPeopleInstance(locationsData);
        cookies = (Map<String,String>) intent.getSerializableExtra("Cookies");
        // create nearby people list checker
        if (cookies != null) {
            CookieReader reader = new CookieReader(cookies);
            reader.run();
            // grab people's data
            ArrayList<People> peopleData = reader.getPeoples();
            if (peopleData != null) {
                peoplesNearby = createNearbyPeopleInstance(peopleData);
            }
        }

        handler.removeCallbacks(sendUpdatesToPhone);
        handler.postDelayed(sendUpdatesToPhone, 0);// 0 = no delay start
        return START_STICKY;
    }

    public void makeNotification(String data) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, getNotification(data));
    }

    private Notification getNotification(String data) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Google Nearby")
                .setContentText(data)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable sendUpdatesToPhone = new Runnable() {
        public void run() {
            // code starts here
            if (cookies != null) {
                CookieReader reader = new CookieReader(cookies);
                reader.run();
                // grab people's data
                ArrayList<People> peopleData = reader.getPeoples();
                StringBuilder message = new StringBuilder();

                if (peopleData != null) {
                    // check saved locations
                    message.append(checkLocation(peopleData, peoplesLocation));
                    message.append(checkNearby(peopleData));

                    if (!message.toString().isEmpty()) {
                        // make notification
                        makeNotification(message.toString());
                    }
                }

            }

            handler.postDelayed(this, 30000); // 30 seconds
        }
    };

    public static ArrayList<PeopleNearby> createNearbyPeopleInstance(ArrayList<People> peopleList) {
        ArrayList<PeopleNearby> temp = new ArrayList<>();

        for (People people: peopleList) {
            Date actual = new Date();
            Timestamp currentTime = new Timestamp(actual.getTime());
            temp.add(new PeopleNearby(people, currentTime, false));
        }

        return temp;
    }

    public static ArrayList<PeopleLocation> createLocationPeopleInstance(String locationsSavedData)
    {
        ArrayList<PeopleLocation> temp = new ArrayList<>();

        // split per person
        String[] persons = locationsSavedData.split("\n\n");
        for (String person: persons) {
            String[] locations = person.split("\n");
            for (String location: locations) {
                String[] locationData = location.split(",");
                /* first and second index has name and place name
                * third and fourth index have the long and lat
                */
                temp.add(new PeopleLocation(locationData[0], locationData[1],
                        Double.parseDouble(locationData[2]), Double.parseDouble(locationData[3])));
            }
        }
        return temp;
    }

    public StringBuilder checkNearby(ArrayList<People> peopleData) {
        StringBuilder message = new StringBuilder();
        // everybody compares their location to the current user
        People currentUser = getCurrentUser(peopleData);
        if (currentUser != null) {
            for (People person: peopleData) {
                PeopleNearby nearbyPerson = isInNearbyList(person.getName());
                // if the nearby person is in our list
                if (nearbyPerson != null && !person.getName().equals("Current User") &&
                        person.getCurrentLocation() != null) {
                    // only see if they're nearby if it's not the nearby user
                    double distance =
                            distanceKilometer(currentUser.getCurrentLocation().getLatitude(),
                                    currentUser.getCurrentLocation().getLongitude(),
                                    person.getCurrentLocation().getLatitude(),
                                    person.getCurrentLocation().getLongitude());
                    boolean isNear = distance <= NEARBY_DISTANCE_KM;
                    // if the timestamp has been passed an hour and the user use to be nearby
                    // and no longer is, or reverse.
                    if (nearbyPerson.canRefresh(isNear)) {
                        // set the new boolean
                        nearbyPerson.setNear(isNear);
                        Date actual = new Date();
                        // add one hour expiration
                        Timestamp currentTime = new Timestamp(actual.getTime() + 3600*1000);
                        // set new timestamp to expire in an hour
                        nearbyPerson.setTimeStamp(currentTime);

                        // build message
                        if (isNear) {
                            message.append(person.getName()).append(" is ")
                                    .append(Math.round(distance * 100000.0) / 100.0)
                                    .append(" meters away");
                        } else {
                            message.append(person.getName()).append(" is no longer nearby");
                        }
                    }
                } else if (nearbyPerson == null) {
                    // if person is not in the nearby list but appeared, add them to the list
                    Date actual = new Date();
                    Timestamp currentTime = new Timestamp(actual.getTime());
                    peoplesNearby.add(new PeopleNearby(person, currentTime, false));
                }
            }
        }

        return message;
    }

    public PeopleNearby isInNearbyList(String name) {
        for (PeopleNearby people: peoplesNearby) {
            if (people.isPersonInList(name)) {
                return people;
            }
        }
        return null;
    }

    public static StringBuilder checkLocation(ArrayList<People> peopleList,
                                       ArrayList<PeopleLocation> locationData) {
        StringBuilder message = new StringBuilder();
        for (PeopleLocation peopleLocation: locationData) {
            People currentPerson = getPersonFromName(peopleList, peopleLocation.getName());
            // now check location difference, make sure person is not null nor their location
            if (currentPerson != null && currentPerson.getCurrentLocation() != null) {
                double distance = distanceKilometer(peopleLocation.getLatitude(),
                        peopleLocation.getLongitude(),
                        currentPerson.getCurrentLocation().getLatitude(),
                        currentPerson.getCurrentLocation().getLongitude());
                // if they just arrived at the location
                if (distance <= LOCATION_DISTANCE_KM && !peopleLocation.isAtLocation()) {
                    peopleLocation.setAtLocation(true);
                    message.append(peopleLocation.getName()).append(" has arrived at ")
                            .append(peopleLocation.getPlace());
                } else if (distance > LOCATION_DISTANCE_KM && peopleLocation.isAtLocation()) {
                    peopleLocation.setAtLocation(false);
                    message.append(peopleLocation.getName()).append(" just left ")
                            .append(peopleLocation.getPlace());
                }
            }
        }

        return message;
    }

    private static People getPersonFromName(ArrayList<People> peopleData, String name) {
        for (People person: peopleData) {
            if (name.equals(person.getName())) {
                return person;
            }
        }
        return null;
    }

    private static People getCurrentUser(ArrayList<People> peopleData) {
        for (People person: peopleData) {
            if (person.getName().equals("Current User")) {
                return person;
            }
        }
        return null;
    }

    private static double distanceKilometer(double lat1, double lon1, double lat2, double lon2) {
        Double p = Math.PI / 180;
        double a = 0.5 - Math.cos((lat2 - lat1) * p) / 2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) * (1 - Math.cos((lon2 - lon1) * p)) / 2;
        return 12742 * Math.asin(Math.sqrt(a));
    }
}
