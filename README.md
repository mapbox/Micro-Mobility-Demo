![image_preview 1](https://user-images.githubusercontent.com/4394910/53114566-e7fa8700-34f8-11e9-91a6-2e60e39501db.gif)


# Micro-Mobility-Demo


External repository housing the open source code for our Micro-mobility app demo


### Building the app on your device

- Clone this repo and open the project in Android Studio.

- Create or log into your Mapbox account and retrieve your default Mapbox access token at [https://www.mapbox.com/account/access-tokens/](https://www.mapbox.com/account/access-tokens/).

- Go to the `R.strings.xml` file in Android Studio (`app/src/main/res/values/strings.xml`) and paste your access token in the `mapbox_access_token` String resource.

```<string name="mapbox_access_token" translatable="false">PASTE_YOUR_MAPBOX_TOKEN_HERE</string>```

- At this point, the project should compile in Android Studio and open on your real or emulated device.

### Miscellaneous info:

- There is no equivalent project for iOS, but the Mapbox Maps SDKs for iOS and Android have "feature parity". What can be done in this project can also be done on iOS devices.

- The app has lots of toggling which leads to various control flow statements (if/else, etc). Don't worry! If you’re building a similar app, your code will probably be cleaner because you’ll decide to permanently include features which are toggleable in this app.

- Bikes and scooters are used in this project, but the project is agnostic to the mode of transportation. The global mobility craze includes pedal bikes, electric mopeds, electric-assisted pedal bikes, electric push scooters, heavy-duty electric push scooters, and more. This project can be adjusted for any type of transportation.


### Contributing

Pull requests and issue reporting are both welcomed! Please see [the contributing guide](https://github.com/mapbox/Micro-Mobility-Demo/blob/master/CONTRIBUTING.md) for more information about contributing to this project.


### Inside the app

<img src="https://user-images.githubusercontent.com/4394910/53115066-1331a600-34fa-11e9-93d8-c7825849bf2a.gif" width="325"/> <img src="https://user-images.githubusercontent.com/4394910/53114927-cea60a80-34f9-11e9-9d5e-cd7156593a77.gif" width="325"/> 

<img src="https://user-images.githubusercontent.com/4394910/53114928-cea60a80-34f9-11e9-94c6-01af0a9c1e32.gif" width="325"/> <img src="https://user-images.githubusercontent.com/4394910/53114930-cea60a80-34f9-11e9-99d7-6504d6a464bb.gif" width="325"/> 
<img src="https://user-images.githubusercontent.com/4394910/53114932-cf3ea100-34f9-11e9-95d9-3e6755703233.gif" width="325"/> 
