# NearMeApp

## Plots users current location on the map and plots users nearby location on the map as well.

### This is a Kotlin Project.

App uses the FusedLocationProviderClient and fetches uses current location.

-*On the bottom left there is a icon, on click of which the app uses FourSquare API to plot users nearby places on the map using markers*

-*On click of each marker on the map, a bottom sheet dialog opens with the venue name and address.*

***Android Components used:***

*Development Language* - Kotlin

*Architecture* - MVVM

*Dependency Injection* - Dagger Hilt

*Networking Libraries* - Retrofit

*Asynchronous calls* - LiveData, Coroutines

*UI* - ConstraintLayout, BottomSheet, GoogleMaps, Custom Markers XML files.

*API For Users Current Location* - FusedLocationProviderClient

*API to plot users nearby Location* - Foursquare Places API (https://api.foursquare.com/v3/places/nearby/<PARAMS>)


