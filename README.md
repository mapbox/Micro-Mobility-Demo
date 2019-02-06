# Micro-Mobility-Demo


External repository housing the open source code for our Micro-mobility app demo


### Building the app on your device

1. Clone this repo and open the project in Android Studio.

2. Create or log into your Mapbox account and retrieve your default Mapbox access token at [https://www.mapbox.com/account/access-tokens/](https://www.mapbox.com/account/access-tokens/).

3. Go to the `R.strings.xml` file in Android Studio (`app/src/main/res/values/strings.xml`) and paste your access token in the `mapbox_access_token` String resource.

```
<string name="mapbox_access_token" translatable="false"> PASTE_YOUR_MAPBOX_TOKEN_HERE </string>
```

4. At this point, the project should compile in Android Studio and open on your real or emulated device.


### Contributing

Pull requests are welcomed! Reported issues are also welcomed.

