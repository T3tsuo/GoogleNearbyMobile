# GoogleNearbyMobile
Looked at what the python library locationsharinglib does and did the bare minimum for what I want an android app to do in Java. Similar to my GoogleNearby project.

## How to get cookies file on Android without a PC.
* First download Kiwi Browser on the Google Playstore (allows extensions).
* Only use one google account on this browser and stay on desktop mode.
* Click on this extention <a href="https://chromewebstore.google.com/detail/get-cookiestxt-locally/cclelndahbckbenkjhflpdbgdldlbecc" target="_blank">Get cookies.txt LOCALLY</a> then Add to Chrome to the Kiwi Browser.
* Go to specifically <a href="https://google.com/maps" target="_blank">google.com/maps</a> and login to the google account you want to use.
    * If you are already logged in: LOGOUT and login again.
    * it can take a while to load the entire page.
* Once logged in. Click on the browsers three dots on the top right and scroll down until you find the Get cookies.txt LOCALLY extension and click it.
* Click on "Export" to extract the cookie file, DO NOT click on "Export As" or "Export All Cookies".

## Saved Locations text file Example:
Purpose: The app will load the saved locations file to check when a person is arriving or leaving the locations you set in the file and then will push a notification to notifying the user.
* Persons' name must match their exact full name on Google Maps.
* The persons locations you want to be notified of should go like this: Full Name,Location,Lat,Long
    * with no spaces between the commas.
* Leave a new line between each person.
```
John Doe,Home,45.6666666,-64.6666666
John Doe,Location2,44.432532345,-63.376574345
John Doe,Location3,lat3,long3
John Doe,Location4,lat4,long4
John Doe,Location5,lat5,long5
John Doe,Location6,lat6,long6

Person2,at Work,45.777777,-64.666666
Person2,Location2,lat2,long2
Person2,Location3,lat3,long3

Person3,at Cottage,44.777777,-65.664465437
Person3,Home,43.444444,-62.333333
```
