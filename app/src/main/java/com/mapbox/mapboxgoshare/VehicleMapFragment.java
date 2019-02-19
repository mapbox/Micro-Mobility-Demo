package com.mapbox.mapboxgoshare;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.turf.TurfConversion;
import com.mapbox.turf.TurfJoins;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.v4.content.ContextCompat.getDrawable;
import static com.mapbox.api.directions.v5.DirectionsCriteria.IMPERIAL;
import static com.mapbox.api.directions.v5.DirectionsCriteria.PROFILE_WALKING;
import static com.mapbox.mapboxgoshare.util.IconIdConstants.INDIVIDUAL_BIKE_ICON_IMAGE_ID;
import static com.mapbox.mapboxgoshare.util.IconIdConstants.INDIVIDUAL_SCOOTER_ICON_IMAGE_ID;
import static com.mapbox.mapboxgoshare.util.IconIdConstants.MOPED_GARAGE_ICON_IMAGE_ID;
import static com.mapbox.mapboxgoshare.util.IconIdConstants.PLACES_PLUGIN_SEARCH_RESULT_SYMBOL_ICON_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.CLUSTER_COUNT_SYMBOL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.HEATMAP_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.INDIVIDUAL_BIKE_SYMBOL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.INDIVIDUAL_SCOOTER_SYMBOL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.MOPED_GARAGE_LOCATION_SYMBOL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.NEIGHBORHOOD_PARKING_ZONE_FILL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.NEIGHBORHOOD_PARKING_ZONE_LINE_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.NO_PARK_ZONE_FILL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.PLACES_PLUGIN_SEARCH_RESULT_SYMBOL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.PREFERRED_PARKING_ZONE_FILL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.SELECTED_LOCK_STATION_SYMBOL_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.LayerIdConstants.WALK_TO_VEHICLE_ROUTE_LINE_LAYER_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.HEATMAP_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.INDIVIDUAL_BIKE_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.INDIVIDUAL_SCOOTER_CLUSTER_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.INDIVIDUAL_SCOOTER_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.MOPED_GARAGE_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.NEIGHBORHOOD_PARK_ZONE_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.NO_PARK_ZONE_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.PLACES_PLUGIN_SEARCH_RESULT_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.PREFERRED_PARKING_ZONE_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.SELECTED_LOCK_STATION_SOURCE_ID;
import static com.mapbox.mapboxgoshare.util.SourceIdConstants.WALK_TO_VEHICLE_ROUTE_SOURCE_ID;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.gte;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.linear;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.expressions.Expression.lt;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.toNumber;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapRadius;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.heatmapWeight;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineDasharray;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

/**
 * This fragment uses the Mapbox Maps SDK for Android to show a map and various data. Scooters, bikes, moped
 * lock stations, heatmap vizualization, data clusters, location search functionality, layer toggling, and
 * runtime map style switching are all displayed in this fragment.
 * <p>
 * The fragment also can also take a user to the TurnByTurnNavigationFragment.
 */
public class VehicleMapFragment extends Fragment implements
  OnMapReadyCallback, PermissionsListener,
  MapboxMap.OnMapClickListener, MapView.OnDidFinishLoadingStyleListener {

  private static final LatLng MAPBOX_SF_OFFICE_LAT_LNG_COORDINATES = new LatLng(37.7912557, -122.396398);
  private static final LatLng MIDDLE_OF_SF_COORDINATES = new LatLng(37.7590, -122.4498);
  private static final int PLACE_SEARCH_REQUEST_CODE_AUTOCOMPLETE = 1;
  private static final int PLACE_SEARCH_RESULT_CODE_AUTOCOMPLETE = -1;
  private static final float MAX_HEATMAP_LAYER_ZOOM = 14f;
  private static final String CITY_LABEL_LAYER_ID = "settlement-label";
  private LocationComponent locationComponent;
  private String TAG = "VehicleMapFragment";
  private MapboxDirections directionsApiClient;
  private MapView mapView;
  private MapboxMap mapboxMap;
  private LatLng currentSelectedVehicleLatLng;
  private SymbolLayer selectedLockStationSymbolLayer;
  private BuildingPlugin buildingPlugin;
  private View view;
  private FragmentActivity context;
  private PermissionsManager permissionsManager;
  private boolean scooterClustersVisible;
  private boolean scooterClustersLayersAlreadyAdded;
  private int visibilityToggleIndex;
  private int mapStyleToggleIndex;
  private boolean garageSelected = false;

  // Adjust this boolean to determine whether the Streets style's water layer gets adjusted to the toolbar color
  private boolean streetsStyleWaterShouldEqualToolbarColor = true;

  /**
   * Don't want to use a Fragment to show turn-by-turn navigation? Pass "true" below to use the
   * Mapbox Navigation SDK for Android's NavigationLauncher. This launches a entirely new activity
   * with a turn-by-turn experience, rather than filling the fragment container with the
   * TurnByTurnNavigationFragment's NavigationView.
   */
  private boolean useNavigationLauncher = false;

  // Adjust this boolean to determine whether the polygon fill areas which represent acceptable parking areas, have
  // outlines so that the areas stand out a bit more.
  private boolean addOutlinesToNeighboorhoodParkingArea = false;

  // Adjust this to set the speed of the animation to change selected moped lock station icon size.
  private long MOPED_GARAGE_ICON_ANIMATION_SPEED = 300;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    Mapbox.getInstance(context, getString(R.string.mapbox_access_token));
    return inflater.inflate(R.layout.vehicle_map_fragment_layout, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    this.view = view;
    view.findViewById(R.id.single_vehicle_distance_and_time_info_cardview).setVisibility(View.INVISIBLE);
    view.findViewById(R.id.single_moped_garage_location_info_cardview).setVisibility(View.INVISIBLE);

    if (!deviceHasInternetConnection()) {
      Toast.makeText(context, R.string.no_connectivity, Toast.LENGTH_LONG).show();
    }
    // Set up the Mapbox map
    mapView = view.findViewById(R.id.main_mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(this);
  }

  @SuppressWarnings( {"MissingPermission"})
  @Override
  public void onMapReady(@NonNull final MapboxMap mapboxMap) {
    mapboxMap.setStyle(Style.MAPBOX_STREETS,
      new Style.OnStyleLoaded() {
        @Override
        public void onStyleLoaded(@NonNull Style style) {
          mapView.addOnDidFinishLoadingStyleListener(VehicleMapFragment.this);
          VehicleMapFragment.this.mapboxMap = mapboxMap;

          if (streetsStyleWaterShouldEqualToolbarColor) {
            changeWaterColorToDifferentColor(style);
          }

          scooterClustersVisible = false;
          scooterClustersLayersAlreadyAdded = false;

          initBuildingPlugin(style);

          setUpMapData(style);

          view.findViewById(R.id.start_turn_by_turn_navigate_to_bike_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (View.VISIBLE == view.findViewById(R.id.single_vehicle_distance_and_time_info_cardview).getVisibility()) {
                view.findViewById(R.id.main_mapView).setVisibility(View.INVISIBLE);

                startNavigation(useNavigationLauncher, currentSelectedVehicleLatLng);
              }
            }
          });

          VehicleMapFragment.this.mapboxMap.addOnMapClickListener(VehicleMapFragment.this);

          view.findViewById(R.id.device_location_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                locationComponent != null && locationComponent.getLastKnownLocation() != null ?
                  new LatLng(locationComponent.getLastKnownLocation()) : MIDDLE_OF_SF_COORDINATES, 15), 15);
            }
          });

          enableLocationComponent(style);

          enableLocalizationPlugin(style);
        }
      });
  }

  @Override
  public void onDidFinishLoadingStyle() {
    if (mapboxMap != null && mapboxMap.getStyle() != null) {
      Style style = mapboxMap.getStyle();
      setUpMapData(style);
      if (style.getUrl().equals(Style.MAPBOX_STREETS)) {
        if (streetsStyleWaterShouldEqualToolbarColor) {
          changeWaterColorToDifferentColor(style);
        }
      }
    }
  }

  private void setUpMapData(@NonNull Style loadedMapStyle) {

    // Remove the large city label layer from the map. Don't need to have the words "San Francisco"
    // always on the map.
    hideLayer(CITY_LABEL_LAYER_ID);

    addLockStationsToMap(loadedMapStyle);
    initUpSelectedLockLocationImageLayer(loadedMapStyle);
    addBikesToMap(loadedMapStyle);
    addScootersToMap(loadedMapStyle);
    addNoParkZonesToMap(loadedMapStyle);
    addPreferredParkingZonesToMap(loadedMapStyle);
    addNeighborhoodParkingZonesToMap(loadedMapStyle, addOutlinesToNeighboorhoodParkingArea);
    addHeatmapData(loadedMapStyle);
    initDashWalkingDirectionLineLayer(loadedMapStyle);

    // Show building extrusions as long as the map style isn't the Mapbox Satellite style or Satellite Streets style
    buildingPlugin.setVisibility(!loadedMapStyle.getUrl().contains("mapbox://styles/mapbox/satellite"));

    // Add a SymbolLayer to eventually drop a marker for a searched location that is found via
    // the Mapbox Places Plugin for Android: https://www.mapbox.com/android-docs/plugins/overview/places/
    addPlaceSearchResultSymbolIconLayer(loadedMapStyle);
  }

  @SuppressWarnings( {"MissingPermission"})
  private void startNavigation(boolean useNavigationLauncher, LatLng selectedDestination) {
    if (useNavigationLauncher) {
      NavigationRoute.builder(context)
        .accessToken(getString(R.string.mapbox_access_token))
        .voiceUnits(IMPERIAL)
        .profile(PROFILE_WALKING)
        .origin(getAppropriateOriginPoint())
        .destination(Point.fromLngLat(selectedDestination.getLongitude(), selectedDestination.getLatitude()))
        .build()
        .getRoute(new Callback<DirectionsResponse>() {
          @Override
          public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
              .directionsRoute(response.body().routes().get(0))
              .directionsProfile(PROFILE_WALKING)
              .shouldSimulateRoute(true)
              .build();
            view.findViewById(R.id.main_mapView).setVisibility(View.INVISIBLE);
            NavigationLauncher.startNavigation(context, options);
          }

          @Override
          public void onFailure(Call<DirectionsResponse> call, Throwable t) {
            Toast.makeText(context, R.string.failure_to_retrieve, Toast.LENGTH_LONG).show();
          }
        });
    } else {
      double originLat = getAppropriateOriginPoint().latitude();
      double originLong = getAppropriateOriginPoint().longitude();
      Bundle args = new Bundle();
      args.putDouble("NAVIGATION_DESTINATION_LAT", selectedDestination.getLatitude());
      args.putDouble("NAVIGATION_DESTINATION_LONG", selectedDestination.getLongitude());
      args.putDouble("NAVIGATION_ORIGIN_LAT", originLat);
      args.putDouble("NAVIGATION_ORIGIN_LONG", originLong);
      TurnByTurnNavigationFragment turnByTurnNavigationFragment = new TurnByTurnNavigationFragment();
      turnByTurnNavigationFragment.setArguments(args);
      FragmentTransaction transaction = context.getFragmentManager().beginTransaction();
      transaction.addToBackStack(null);
      transaction.replace(R.id.map_fragment_container, turnByTurnNavigationFragment);
      transaction.commit();
    }
  }

  @SuppressWarnings( {"MissingPermission"})
  private boolean locationIsInSanFranciscoBounds() {
    if (locationComponent != null && locationComponent.getLastKnownLocation() != null) {
      List<List<Point>> listOfPolygonPoints = new ArrayList<>();
      List<Point> polygonPoints = new ArrayList<>();
      polygonPoints.add(Point.fromLngLat(-122.477, 37.810));
      polygonPoints.add(Point.fromLngLat(-122.486, 37.7890));
      polygonPoints.add(Point.fromLngLat(-122.509, 37.786));
      polygonPoints.add(Point.fromLngLat(-122.515, 37.781));
      polygonPoints.add(Point.fromLngLat(-122.498, 37.6889));
      polygonPoints.add(Point.fromLngLat(-122.490, 37.630));
      polygonPoints.add(Point.fromLngLat(-122.405, 37.582));
      polygonPoints.add(Point.fromLngLat(-122.357, 37.590));
      polygonPoints.add(Point.fromLngLat(-122.381, 37.6109));
      polygonPoints.add(Point.fromLngLat(-122.355, 37.6140));
      polygonPoints.add(Point.fromLngLat(-122.348, 37.83));
      polygonPoints.add(Point.fromLngLat(-122.460, 37.833));
      listOfPolygonPoints.add(polygonPoints);
      return TurfJoins.inside(Point.fromLngLat(locationComponent.getLastKnownLocation().getLongitude(),
        locationComponent.getLastKnownLocation().getLatitude()), Polygon.fromLngLats(listOfPolygonPoints));
    }
    return false;
  }

  private void addLockStationsToMap(@NonNull Style loadedMapStyle) {
    loadedMapStyle.addImage(MOPED_GARAGE_ICON_IMAGE_ID, BitmapFactory.decodeResource(
      context.getResources(), R.drawable.lock_station_lock));

    // Use the Mapbox Dataset and Tileset sections of your Mapbox Studio account to pull in
    // your own data.
    loadedMapStyle.addSource(new VectorSource(MOPED_GARAGE_SOURCE_ID,
      "mapbox://langsmith.cjf71u2ms13is2wpjaoa85myz-2a7qw"));
    SymbolLayer lockStationSymbolLayer = new SymbolLayer(MOPED_GARAGE_LOCATION_SYMBOL_LAYER_ID,
      MOPED_GARAGE_SOURCE_ID)
      .withProperties(
        iconImage(MOPED_GARAGE_ICON_IMAGE_ID),
        iconAllowOverlap(true),
        iconIgnorePlacement(true)
      );
    lockStationSymbolLayer.setSourceLayer("GoShare_Bike_Station_Locations");
    loadedMapStyle.addLayer(lockStationSymbolLayer);
  }

  private void addPlaceSearchResultSymbolIconLayer(@NonNull Style loadedMapStyle) {
    loadedMapStyle.addImage(PLACES_PLUGIN_SEARCH_RESULT_SYMBOL_ICON_ID, BitmapFactory.decodeResource(context.getResources(),
      R.drawable.mapbox_marker_icon_default));
    loadedMapStyle.addSource(new GeoJsonSource(PLACES_PLUGIN_SEARCH_RESULT_SOURCE_ID));
    loadedMapStyle.addLayer(new SymbolLayer(PLACES_PLUGIN_SEARCH_RESULT_SYMBOL_LAYER_ID,
      PLACES_PLUGIN_SEARCH_RESULT_SOURCE_ID).withProperties(
      iconImage(PLACES_PLUGIN_SEARCH_RESULT_SYMBOL_ICON_ID),
      iconSize(1.2f),
      iconAllowOverlap(true),
      iconIgnorePlacement(true)
    ));
  }

  private void addBikesToMap(@NonNull Style loadedMapStyle) {
    loadedMapStyle.addImage(INDIVIDUAL_BIKE_ICON_IMAGE_ID, BitmapFactory.decodeResource(
      context.getResources(), R.drawable.bike_icon));
    // Use the Mapbox Dataset and Tileset sections of your Mapbox Studio account to pull in
    // your own data.
    loadedMapStyle.addSource(new VectorSource(INDIVIDUAL_BIKE_SOURCE_ID,
      "mapbox://langsmith.cjf7bdm5a110a30mcclh7p0rj-06ykj"));
    SymbolLayer bikeLocationSymbolLayer = new SymbolLayer(INDIVIDUAL_BIKE_SYMBOL_LAYER_ID,
      INDIVIDUAL_BIKE_SOURCE_ID)
      .withProperties(
        iconImage(INDIVIDUAL_BIKE_ICON_IMAGE_ID),
        iconAllowOverlap(true),
        iconIgnorePlacement(true)
      );
    bikeLocationSymbolLayer.setSourceLayer("GoShare_Bike_Locations");
    loadedMapStyle.addLayer(bikeLocationSymbolLayer);
  }

  private void addScootersToMap(@NonNull Style loadedMapStyle) {
    loadedMapStyle.addImage(INDIVIDUAL_SCOOTER_ICON_IMAGE_ID, BitmapFactory.decodeResource(
      context.getResources(), R.drawable.scooter_icon));
    // Use the Mapbox Dataset and Tileset sections of your Mapbox Studio account to pull in
    // your own data.
    loadedMapStyle.addSource(new VectorSource(INDIVIDUAL_SCOOTER_SOURCE_ID,
      "mapbox://langsmith.cjfakhw5v18q83uo9n6we6x29-2t9cx"));
    SymbolLayer scooterSymbolLayer = new SymbolLayer(INDIVIDUAL_SCOOTER_SYMBOL_LAYER_ID,
      INDIVIDUAL_SCOOTER_SOURCE_ID)
      .withProperties(
        iconImage(INDIVIDUAL_SCOOTER_ICON_IMAGE_ID),
        iconAllowOverlap(true),
        iconIgnorePlacement(true));
    scooterSymbolLayer.setSourceLayer("GoShare_Scooter_Locations");
    loadedMapStyle.addLayer(scooterSymbolLayer);
  }

  private void hideLayer(String layerName) {
    if (mapboxMap.getStyle() != null) {
      try {
        mapboxMap.getStyle().getLayer(layerName).setProperties(visibility(NONE));
      } catch (NullPointerException exception) {
        Log.d(TAG, "hideLayer: " + exception);
      }
    }
  }

  private void showLayer(String layerName) {
    if (mapboxMap.getStyle() != null) {
      try {
        mapboxMap.getStyle().getLayer(layerName).setProperties(visibility(VISIBLE));
      } catch (NullPointerException exception) {
        Log.d(TAG, "showLayer: " + exception);
      }
    }
  }

  private void removeLayerFromMap(String layerName) {
    mapboxMap.getStyle().removeLayer(layerName);
  }

  private void initBuildingPlugin(@NonNull Style loadedMapStyle) {
    buildingPlugin = new BuildingPlugin(mapView, mapboxMap, loadedMapStyle);
    buildingPlugin.setVisibility(true);
  }

  private String loadGeoJsonFromAsset(String filename) {
    try {
      // Load GeoJSON file
      InputStream is = context.getAssets().open(filename);
      int size = is.available();
      byte[] buffer = new byte[size];
      is.read(buffer);
      is.close();
      return new String(buffer, "UTF-8");

    } catch (Exception exception) {
      Log.e("VehicleMapFragment", "Exception loading GeoJSON: " + exception.toString());
      exception.printStackTrace();
      return null;
    }
  }

  /**
   * Especially if you're loading a locally-stored file with lots of GeoJSON data, it'd be better to use an
   * AsyncTask to load the data. This is a better option than the more straightforward loadGeoJsonFromAsset()
   * method above.
   */
  // TODO: Finish this
  /*private static class LoadVehicleGeoJson extends AsyncTask<Void, Void, FeatureCollection> {
    private WeakReference<Activity> weakReference;
    private String geoJsonFileName;

    LoadVehicleGeoJson(Activity activity, String geoJsonFileName) {
      this.weakReference = new WeakReference<>(activity);
      this.geoJsonFileName = geoJsonFileName;
    }

    @Override
    protected FeatureCollection doInBackground(Void... voids) {
      try {
        Activity activity = weakReference.get();
        if (activity != null) {
          InputStream inputStream = activity.getAssets().open(geoJsonFileName);
          return FeatureCollection.fromJson(convertStreamToString(inputStream));
        }
      } catch (Exception exception) {
        Log.e("LoadVehicleGeoJson", "Exception Loading GeoJSON: " + exception.toString());
      }
      return null;
    }

    static String convertStreamToString(InputStream is) {
      Scanner scanner = new Scanner(is).useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }

    @Override
    protected void onPostExecute(@Nullable FeatureCollection featureCollection) {
      super.onPostExecute(featureCollection);
      Activity activity = weakReference.get();
      if (activity != null && featureCollection != null) {
        activity.addBikeLocationsSourceToMap(featureCollection);
      }
    }
  }*/
  private void setUpClusters(String sourceId) {
    int[][] scooterLayers = new int[][] {
      new int[] {150, Color.parseColor("#8046CA")},
      new int[] {20, Color.parseColor("#8046CA")},
      new int[] {0, Color.parseColor("#8046CA")}
    };
    for (int i = 0; i < scooterLayers.length; i++) {

      //Add clusters' circles
      CircleLayer circleClusterLayer = new CircleLayer(sourceId + "cluster-" + i, sourceId);
      circleClusterLayer.setProperties(
        circleColor(scooterLayers[0][1]),
        circleRadius(14f)
      );

      Expression pointCount = toNumber(get("point_count"));

      // Add a filter to the cluster layer that hides the circles based on "point_count"
      circleClusterLayer.setFilter(
        i == 0
          ? gte(pointCount, literal(scooterLayers[i][0])) :
          Expression.all(
            gte(pointCount, literal(scooterLayers[i][0])),
            lt(pointCount, literal(scooterLayers[i - 1][0])
            )
          ));
      mapboxMap.getStyle().addLayer(circleClusterLayer);
    }

    //Add the count labels
    SymbolLayer count = new SymbolLayer(CLUSTER_COUNT_SYMBOL_LAYER_ID, sourceId);
    count.withProperties(
      textField("{point_count}"),
      textSize(12f),
      textColor(Color.WHITE),
      textIgnorePlacement(true),
      textAllowOverlap(true)
    );
    mapboxMap.getStyle().addLayer(count);
  }

  private void addNoParkZonesToMap(@NonNull Style loadedMapStyle) {
    // Use the Mapbox Dataset and Tileset sections of your Mapbox Studio account to pull in
    // your own data.
    loadedMapStyle.addSource(new VectorSource(NO_PARK_ZONE_SOURCE_ID,
      "mapbox://langsmith.cjf7bqhgv129i2xqj6984i9z4-841we"));
    FillLayer noParkingZoneFillLayer = new FillLayer(NO_PARK_ZONE_FILL_LAYER_ID, NO_PARK_ZONE_SOURCE_ID)
      .withProperties(
        fillOpacity(interpolate(exponential(1f), zoom(),
          stop(5, 0f),
          stop(12, .25f),
          stop(18f, 1f))),
        fillColor(Color.parseColor("#EE1717")));
    noParkingZoneFillLayer.setSourceLayer("GoShare_Bike_No_Go_Zones");
    loadedMapStyle.addLayerBelow(noParkingZoneFillLayer, INDIVIDUAL_BIKE_SYMBOL_LAYER_ID);
  }

  private void addPreferredParkingZonesToMap(@NonNull Style loadedMapStyle) {
    // Use the Mapbox Dataset and Tileset sections of your Mapbox Studio account to pull in
    // your own data.
    loadedMapStyle.addSource(new VectorSource(PREFERRED_PARKING_ZONE_SOURCE_ID,
      "mapbox://langsmith.cjf7nijrv18a633mzibqwj0a5-2a2gm"));
    FillLayer preferredParkingFillLayer = new FillLayer(PREFERRED_PARKING_ZONE_FILL_LAYER_ID,
      PREFERRED_PARKING_ZONE_SOURCE_ID)
      .withProperties(
        fillOpacity(0.4f),
        fillColor(Color.parseColor("#FFA500")));
    preferredParkingFillLayer.setSourceLayer("GoShare_Bike_Preferred_Parking_Z");
    loadedMapStyle.addLayerBelow(preferredParkingFillLayer, INDIVIDUAL_BIKE_SYMBOL_LAYER_ID);
  }

  private void addNeighborhoodParkingZonesToMap(@NonNull Style loadedMapStyle, @NonNull boolean addLineOutline) {
    // Use the Mapbox Dataset and Tileset sections of your Mapbox Studio account to pull in
    // your own data.
    loadedMapStyle.addSource(new VectorSource(NEIGHBORHOOD_PARK_ZONE_SOURCE_ID,
      "mapbox://langsmith.cjf7o0ajv084v3uojct9q3k8b-0o6pu"));
    FillLayer neighborhoodParkingZone = new FillLayer(NEIGHBORHOOD_PARKING_ZONE_FILL_LAYER_ID,
      NEIGHBORHOOD_PARK_ZONE_SOURCE_ID)
      .withProperties(fillOpacity(0.4f),
        fillColor(Color.parseColor("#45AAE9")));
    neighborhoodParkingZone.setSourceLayer("GoShare_Bike_Neighborhood_Parkin");
    loadedMapStyle.addLayerBelow(neighborhoodParkingZone, NO_PARK_ZONE_FILL_LAYER_ID);

    if (addLineOutline) {
      LineLayer neighborhoodZoneLineOutline = new LineLayer(NEIGHBORHOOD_PARKING_ZONE_LINE_LAYER_ID,
        NEIGHBORHOOD_PARK_ZONE_SOURCE_ID)
        .withProperties(
          lineWidth(2f),
          lineCap(LINE_CAP_ROUND),
          lineJoin(LINE_JOIN_ROUND),
          lineColor(Color.parseColor("#006db2")));
      neighborhoodZoneLineOutline.setSourceLayer("GoShare_Bike_Neighborhood_Parkin");
      loadedMapStyle.addLayerBelow(neighborhoodZoneLineOutline, NO_PARK_ZONE_FILL_LAYER_ID);
    }
  }

  private void addHeatmapData(@NonNull Style loadedMapStyle) {
    // Use the Mapbox Dataset and Tileset sections of your Mapbox Studio account to pull in
    // your own data.
    loadedMapStyle.addSource(new VectorSource(HEATMAP_SOURCE_ID,
      "mapbox://langsmith.cjfsu62ap010i2qrxti214ub6-3pa5v"));
    HeatmapLayer heatmapLayer = new HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID)
      .withProperties(
        // See https://docs.mapbox.com/android/maps/examples/add-multiple-heatmap-styles/
        // for more examples of heatmap styling options
        heatmapWeight(interpolate(linear(), zoom(),
          stop(0, 4),
          stop(6, 10),
          stop(MAX_HEATMAP_LAYER_ZOOM, 12))),
        heatmapOpacity(.6f),
        heatmapRadius(interpolate(linear(), zoom(),
          stop(0, 2),
          stop(9, 7),
          stop(MAX_HEATMAP_LAYER_ZOOM, 12))));
    heatmapLayer.setMaxZoom(MAX_HEATMAP_LAYER_ZOOM);
    heatmapLayer.setSourceLayer("GoShare_Heatmap_Locations");
    loadedMapStyle.addLayerBelow(heatmapLayer, NO_PARK_ZONE_FILL_LAYER_ID);
  }

  private void drawNavigationPolylineRoute(DirectionsRoute route) {
    if (mapboxMap.getStyle() != null) {
      GeoJsonSource navLineRouteSource = mapboxMap.getStyle().getSourceAs(WALK_TO_VEHICLE_ROUTE_SOURCE_ID);
      if (navLineRouteSource != null) {
        navLineRouteSource.setGeoJson(Feature.fromGeometry(LineString.fromPolyline(
          route.geometry(), Constants.PRECISION_6)));
      }
    }
  }

  private void initDashWalkingDirectionLineLayer(@NonNull Style loadedMapStyle) {
    loadedMapStyle.addSource(new GeoJsonSource(WALK_TO_VEHICLE_ROUTE_SOURCE_ID));
    loadedMapStyle.addLayerAbove(new LineLayer(WALK_TO_VEHICLE_ROUTE_LINE_LAYER_ID,
      WALK_TO_VEHICLE_ROUTE_SOURCE_ID)
      .withProperties(
        lineWidth(6f),
        lineOpacity(.6f),
        lineCap(LINE_CAP_ROUND),
        lineJoin(LINE_JOIN_ROUND),
        lineColor(Color.parseColor("#d742f4")),
        lineDasharray(new Float[] {1f, 2f})), NEIGHBORHOOD_PARKING_ZONE_FILL_LAYER_ID);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (mapboxMap.getStyle() != null) {
      Style mapStyle = mapboxMap.getStyle();
      int id = item.getItemId();
      if (id == R.id.toggle_moped_garage_locations_visibility) {

        Layer mopedGarageLocationSymbolLayer = mapStyle.getLayer(MOPED_GARAGE_LOCATION_SYMBOL_LAYER_ID);

        if (mopedGarageLocationSymbolLayer == null) {
          addLockStationsToMap(mapStyle);
        }
        if (VISIBLE.equals(mopedGarageLocationSymbolLayer.getVisibility().getValue())) {
          // Layer is visible
          mopedGarageLocationSymbolLayer.setProperties(
            visibility(NONE)
          );
          selectedLockStationSymbolLayer.setProperties(
            visibility(NONE)
          );
        } else {
          // Layer isn't visible
          mopedGarageLocationSymbolLayer.setProperties(
            visibility(VISIBLE)
          );
          selectedLockStationSymbolLayer.setProperties(
            visibility(VISIBLE)
          );
        }
      } else if (id == R.id.toggle_vehicle_visibility) {
        Layer individualBikeStationLayer = mapStyle.getLayer(INDIVIDUAL_BIKE_SYMBOL_LAYER_ID);
        Layer individualScooterStationLayer = mapStyle.getLayer(INDIVIDUAL_SCOOTER_SYMBOL_LAYER_ID);
        if (individualBikeStationLayer == null || individualScooterStationLayer == null) {
          return true;
        }
        switch (visibilityToggleIndex) {
          case 0:
            individualBikeStationLayer.setProperties(
              visibility(NONE)
            );
            individualScooterStationLayer.setProperties(
              visibility(VISIBLE)
            );
            visibilityToggleIndex++;
            break;
          case 1:
            individualBikeStationLayer.setProperties(
              visibility(VISIBLE)
            );
            individualScooterStationLayer.setProperties(
              visibility(NONE)
            );
            visibilityToggleIndex++;
            break;
          case 2:
            individualBikeStationLayer.setProperties(
              visibility(NONE)
            );
            individualScooterStationLayer.setProperties(
              visibility(NONE)
            );
            visibilityToggleIndex++;
            break;
          case 3:
            individualBikeStationLayer.setProperties(
              visibility(VISIBLE)
            );
            individualScooterStationLayer.setProperties(
              visibility(VISIBLE)
            );
            visibilityToggleIndex = 0;
            break;
        }
        return true;
      } else if (id == R.id.search) {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
          .accessToken(getString(R.string.mapbox_access_token))
          .placeOptions(PlaceOptions.builder()
            .backgroundColor(Color.parseColor("#EEEEEE"))
            .limit(10)
            .build(PlaceOptions.MODE_CARDS))
          .build(context);
        startActivityForResult(intent, PLACE_SEARCH_REQUEST_CODE_AUTOCOMPLETE);
      } else if (id == R.id.cluster_toggle) {
        if (scooterClustersVisible) {
          removeClusterLayersFromMap("scooter cluster-");
          removeLayerFromMap(CLUSTER_COUNT_SYMBOL_LAYER_ID);
          showLayer(INDIVIDUAL_SCOOTER_SYMBOL_LAYER_ID);
          scooterClustersVisible = false;
        } else {
          if (mapStyle.getLayer(INDIVIDUAL_SCOOTER_SYMBOL_LAYER_ID) != null
            && VISIBLE.equals(mapStyle.getLayer(INDIVIDUAL_SCOOTER_SYMBOL_LAYER_ID).getVisibility().getValue())) {

            // Remove the scooter layer which shows the individual scooter locations without clustering functionality.
            hideLayer(INDIVIDUAL_SCOOTER_SYMBOL_LAYER_ID);
            if (!scooterClustersLayersAlreadyAdded) {
              /**
               *
               * Because GeoJsonOptions clustering options need to be set, a GeoJsonSource is used rather than a VectorSource.
               * These options can only be set with a GeoJsonSource.
               *
               * The Mapbox Datasets API could be used to retrieve the list of features, which represent the locations of the
               * individual scooters. When using the Mapbox Datasets API, the Mapbox access token used in this project,
               * needs to match the account which holds the dataset file. This would prohibit you from loading this dataset
               * from the get-go. This limits the flexibility for anyone to use this code,
               * which is why this particular dataset is being loaded via a locally stored file "scooter-locations.geojson".
               *
               * You can upload the file to the Dataset Editor section on your Mapbox account: https://studio.mapbox.com/datasets
               *
               * Then build a GeoJsonSource like below. Make sure to fill in your username and the dataset ID:
               *
               * try {
               *       loadedMapStyle.addSource(new GeoJsonSource(INDIVIDUAL_SCOOTER_SOURCE_ID, new URL(
               *         "https://api.mapbox.com/datasets/v1/YOUR-MAPBOX-ACCOUNT-USERNAME-HERE/DATASET-ID-HERE/features?access_token="
               *           + getString(R.string.mapbox_access_token)),
               *         new GeoJsonOptions()
               *           .withCluster(true)
               *           .withClusterMaxZoom(14)
               *           .withClusterRadius(50)));
               *
               *      setUpClusters(INDIVIDUAL_SCOOTER_CLUSTER_SOURCE_ID);
               *      scooterClustersVisible = true;
               *      scooterClustersLayersAlreadyAdded = true;
               *
               * } catch (MalformedURLException exception) {
               *      Log.d(TAG, "addScootersToMap: " + exception);
               * }
               *
               *
               **/
              mapStyle.addSource(new GeoJsonSource(INDIVIDUAL_SCOOTER_CLUSTER_SOURCE_ID,
                loadGeoJsonFromAsset("scooter-locations.geojson"),
                new GeoJsonOptions()
                  .withCluster(true)
                  .withClusterMaxZoom(14)
                  .withClusterRadius(50)
              ));

              setUpClusters(INDIVIDUAL_SCOOTER_CLUSTER_SOURCE_ID);
              scooterClustersVisible = true;
              scooterClustersLayersAlreadyAdded = true;
            } else {
              setClusterLayersVisibilityToVisible("scooter cluster");
              scooterClustersVisible = true;
            }
          }
        }
      } else if (id == R.id.heat_map_toggle) {
        Layer heatmapLayer = mapStyle.getLayer(HEATMAP_LAYER_ID);
        heatmapLayer.setProperties(visibility(VISIBLE.equals(heatmapLayer.getVisibility().getValue()) ? NONE : VISIBLE));
      } else if (id == R.id.map_style_toggle) {
        switch (mapStyleToggleIndex) {
          case 0:
            mapboxMap.setStyle(Style.MAPBOX_STREETS);
            mapStyleToggleIndex++;
            break;
          case 1:
            mapboxMap.setStyle(Style.LIGHT);
            mapStyleToggleIndex++;
            break;
          case 2:
            mapboxMap.setStyle(Style.DARK);
            mapStyleToggleIndex++;
            break;
          case 3:
            mapboxMap.setStyle(Style.TRAFFIC_DAY);
            mapStyleToggleIndex++;
            break;
          case 4:
            mapboxMap.setStyle(Style.TRAFFIC_NIGHT);
            mapStyleToggleIndex++;
            break;
          case 5:
            mapboxMap.setStyle(Style.SATELLITE_STREETS);
            mapStyleToggleIndex = 0;
        }
        return true;
      } else if (id == R.id.open_camera) {

        // Your code for launching the camera goes here.

        // https://github.com/yuriy-budiyev/code-scanner
        // and https://github.com/zxing/zxing are recommended QR code scanning libraries.

        Toast.makeText(context, R.string.add_camera_code, Toast.LENGTH_SHORT).show();
      }
    }
    return super.onOptionsItemSelected(item);
  }

  private void changeWaterColorToDifferentColor(@NonNull Style loadedMapStyle) {
    // Darken the water color map layer so that the light blue parking neighborhood color doesn't match the water color
    int toolbarColor = ((ColorDrawable) context.findViewById(R.id.toolbar).getBackground()).getColor();
    loadedMapStyle.getLayer("water").setProperties(fillColor(toolbarColor));
  }

  private void setClusterLayerVisibilityToNone(String containString) {
    for (Layer singleLayer : mapboxMap.getStyle().getLayers()) {
      if (singleLayer.getId().contains(containString)) {
        hideLayer(singleLayer.getId());
      }
    }
  }

  private void removeClusterLayersFromMap(String containString) {
    for (Layer singleLayer : mapboxMap.getStyle().getLayers()) {
      if (singleLayer.getId().contains(containString)) {
        removeLayerFromMap(singleLayer.getId());
      }
    }
  }

  private void setClusterLayersVisibilityToVisible(String containString) {
    for (Layer singleLayer : mapboxMap.getStyle().getLayers()) {
      if (singleLayer.getId().contains(containString)) {
        showLayer(singleLayer.getId());
      }
    }
  }

  @Override
  public boolean onMapClick(@NonNull LatLng point) {
    List<Feature> bikeFeatures = getRenderedFeatures(
      mapboxMap.getProjection().toScreenLocation(point), INDIVIDUAL_BIKE_SYMBOL_LAYER_ID);
    List<Feature> scooterFeatures = getRenderedFeatures(
      mapboxMap.getProjection().toScreenLocation(point), INDIVIDUAL_SCOOTER_SYMBOL_LAYER_ID);
    List<Feature> mopedGarageLocations = getRenderedFeatures(
      mapboxMap.getProjection().toScreenLocation(point), MOPED_GARAGE_LOCATION_SYMBOL_LAYER_ID);
    view.findViewById(R.id.single_moped_garage_location_info_cardview).setVisibility(View.INVISIBLE);
    view.findViewById(R.id.single_vehicle_distance_and_time_info_cardview).setVisibility(View.INVISIBLE);
    if (!bikeFeatures.isEmpty()) {
      evaluateClick(bikeFeatures, R.drawable.bike_icon, false);
    }
    if (!scooterFeatures.isEmpty()) {
      evaluateClick(scooterFeatures, R.drawable.scooter_icon, true);
    }
    if (!mopedGarageLocations.isEmpty()) {
      setMopedGarageCardview();
      adjustCompass(true);
      final SymbolLayer selectedGarageLayer = (SymbolLayer) mapboxMap.getStyle().getLayer(SELECTED_LOCK_STATION_SYMBOL_LAYER_ID);
      List<Feature> selectedFeature = mapboxMap.queryRenderedFeatures(
        mapboxMap.getProjection().toScreenLocation(point), SELECTED_LOCK_STATION_SYMBOL_LAYER_ID);
      if (selectedFeature.size() > 0 && garageSelected) {
        return true;
      }
      GeoJsonSource source = mapboxMap.getStyle().getSourceAs(SELECTED_LOCK_STATION_SOURCE_ID);
      if (source != null) {
        source.setGeoJson(FeatureCollection.fromFeatures(
          new Feature[] {Feature.fromGeometry(mopedGarageLocations.get(0).geometry())}));
      }
      if (garageSelected) {
        decreaseIconSize(selectedGarageLayer);
      }
      if (mopedGarageLocations.size() > 0) {
        increaseIconSize(selectedGarageLayer);
      }
    }
    if (bikeFeatures.isEmpty() && scooterFeatures.isEmpty() && mopedGarageLocations.isEmpty()) {
      view.findViewById(R.id.single_vehicle_distance_and_time_info_cardview).setVisibility(View.INVISIBLE);
      view.findViewById(R.id.single_moped_garage_location_info_cardview).setVisibility(View.INVISIBLE);
      adjustCompass(false);
    }
    return true;
  }

  private void evaluateClick(List<Feature> featureList, int drawable, boolean scooterSelected) {
    Feature feature = featureList.get(0);
    setVehicleCardview(drawable, scooterSelected);
    Point singleLocationPoint = (Point) feature.geometry();
    currentSelectedVehicleLatLng = new LatLng(singleLocationPoint.coordinates().get(1),
      singleLocationPoint.coordinates().get(0));
    getInformationFromDirectionsApi(currentSelectedVehicleLatLng);
    adjustCameraZoom();
    adjustCompass(true);
  }

  private void increaseIconSize(final SymbolLayer symbolLayer) {
    ValueAnimator symbolLayerIconAnimator = new ValueAnimator();
    symbolLayerIconAnimator.setObjectValues(1f, 1.4f);
    symbolLayerIconAnimator.setDuration(MOPED_GARAGE_ICON_ANIMATION_SPEED);
    symbolLayerIconAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animator) {
        symbolLayer.setProperties(
          iconSize((float) animator.getAnimatedValue())
        );
      }
    });
    symbolLayerIconAnimator.start();
    garageSelected = true;
  }

  private void decreaseIconSize(final SymbolLayer symbolLayer) {
    ValueAnimator symbolLayerIconAnimator = new ValueAnimator();
    symbolLayerIconAnimator.setObjectValues(1.4f, 1f);
    symbolLayerIconAnimator.setDuration(MOPED_GARAGE_ICON_ANIMATION_SPEED);
    symbolLayerIconAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animator) {
        symbolLayer.setProperties(
          iconSize((float) animator.getAnimatedValue())
        );
      }
    });
    symbolLayerIconAnimator.start();
    garageSelected = false;
  }

  private void adjustCompass(boolean lowerBelowCardview) {
    mapboxMap.getUiSettings().setCompassMargins(mapboxMap.getUiSettings().getCompassMarginLeft(),
      lowerBelowCardview ? view.findViewById(R.id.single_vehicle_distance_and_time_info_cardview).getMeasuredHeight()
        + 30 : 0,
      mapboxMap.getUiSettings().getCompassMarginRight(),
      mapboxMap.getUiSettings().getCompassMarginBottom());
  }

  private void adjustCameraZoom() {
    mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
      new LatLng(currentSelectedVehicleLatLng.getLatitude(), currentSelectedVehicleLatLng.getLongitude()),
      mapboxMap.getCameraPosition().zoom < 12 ? 14 : mapboxMap.getCameraPosition().zoom), 1800);
  }

  private List<Feature> getRenderedFeatures(PointF screenPoint, String layerId) {
    return mapboxMap.queryRenderedFeatures(
      screenPoint, layerId);
  }

  private void initUpSelectedLockLocationImageLayer(@NonNull Style loadedMapStyle) {
    FeatureCollection emptySource = FeatureCollection.fromFeatures(new Feature[] {});
    Source selectedMarkerSource = new GeoJsonSource(SELECTED_LOCK_STATION_SOURCE_ID, emptySource);
    loadedMapStyle.addSource(selectedMarkerSource);
    selectedLockStationSymbolLayer = new SymbolLayer(SELECTED_LOCK_STATION_SYMBOL_LAYER_ID,
      SELECTED_LOCK_STATION_SOURCE_ID);
    selectedLockStationSymbolLayer.withProperties(
      iconImage(MOPED_GARAGE_ICON_IMAGE_ID),
      iconAllowOverlap(true),
      iconIgnorePlacement(true));
    loadedMapStyle.addLayer(selectedLockStationSymbolLayer);
  }

  @SuppressWarnings( {"MissingPermission"})
  private void getInformationFromDirectionsApi(@NonNull final LatLng selectedVehicleCoordinates) {
    directionsApiClient = MapboxDirections.builder()
      .origin(getAppropriateOriginPoint())
      .destination(Point.fromLngLat(selectedVehicleCoordinates.getLongitude(),
        selectedVehicleCoordinates.getLatitude()))
      .overview(DirectionsCriteria.OVERVIEW_FULL)
      .profile(PROFILE_WALKING)
      .accessToken(getString(R.string.mapbox_access_token))
      .build();

    directionsApiClient.enqueueCall(new Callback<DirectionsResponse>() {
      @Override
      public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        // Check that the response isn't null and that the response has a route
        if (response.body() == null) {
          Log.d(TAG, getString(R.string.set_right_token));
        } else if (response.body().routes().size() < 1) {
          Log.d(TAG, getString(R.string.no_routes_found));
        } else {
          // Retrieve and draw the navigation route on the map
          drawNavigationPolylineRoute(response.body().routes().get(0));
          setVehicleWalkToDistance(response.body());
          setVehicleWalkToTime(response.body());
          getVehicleLocation(Point.fromLngLat(selectedVehicleCoordinates.getLongitude(),
            selectedVehicleCoordinates.getLatitude()));
        }
      }

      @Override
      public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
        Toast.makeText(context, R.string.failure_to_retrieve, Toast.LENGTH_LONG).show();
      }
    });
  }

  @SuppressWarnings( {"MissingPermission"})
  private Point getAppropriateOriginPoint() {
    Point originPoint;
    if (locationComponent != null) {
      if (locationIsInSanFranciscoBounds()) {
        originPoint = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(),
          mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
      } else {
        originPoint = Point.fromLngLat(MAPBOX_SF_OFFICE_LAT_LNG_COORDINATES.getLongitude(),
          MAPBOX_SF_OFFICE_LAT_LNG_COORDINATES.getLatitude());
      }
    } else {
      originPoint = Point.fromLngLat(MAPBOX_SF_OFFICE_LAT_LNG_COORDINATES.getLongitude(),
        MAPBOX_SF_OFFICE_LAT_LNG_COORDINATES.getLatitude());
    }
    return originPoint;
  }

  private void getVehicleLocation(Point vehicleLocation) {
    MapboxGeocoding.builder()
      .accessToken(getString(R.string.mapbox_access_token))
      .query(Point.fromLngLat(vehicleLocation.longitude(), vehicleLocation.latitude()))
      .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
      .build()
      .enqueueCall(new Callback<GeocodingResponse>() {
        @Override
        public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
          List<CarmenFeature> results = response.body().features();
          if (results.size() > 0) {
            TextView vehicleAddress = view.findViewById(R.id.vehicle_address);
            vehicleAddress.setText(results.get(0).address() != null ?
              results.get(0).placeName().split(",")[0] : getString(R.string.address_unknown));
          } else {
            // No result for your request were found.
            Log.d(TAG, "onResponse: No result found");
          }
        }

        @Override
        public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
          throwable.printStackTrace();
        }
      });
  }

  private void setVehicleWalkToDistance(DirectionsResponse response) {
    TextView walkingDistance = view.findViewById(R.id.mileage_walking_distance_to_vechicle);
    if (response.routes().get(0).distance() != null) {
      walkingDistance.setText(" " + String.format(getString(R.string.walking_miles),
        new DecimalFormat("0.0").format(TurfConversion.convertLength(
          response.routes().get(0).distance(),
          "meters", "miles"))));
    } else {
      walkingDistance.setText(getString(R.string.travel_time_unknown));
    }
  }

  private void setVehicleWalkToTime(DirectionsResponse response) {
    TextView walkingTime = view.findViewById(R.id.walking_time_to_vechicle);
    DecimalFormat decimalFormat;
    if (response.routes().get(0).duration() != null) {
      decimalFormat = new DecimalFormat(response.routes().get(0).duration() / 60 >= 10 ? "00" : "0");
      walkingTime.setText(String.format(getString(R.string.walking_minutes), decimalFormat.format(
        response.routes().get(0).duration() / 60)));
    } else {
      walkingTime.setText(getString(R.string.travel_time_unknown));
    }
  }

  @Override
  public void onExplanationNeeded(List<String> permissionsToExplain) {
    Toast.makeText(context, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
  }

  @SuppressWarnings( {"MissingPermission"})
  private void enableLocationComponent(@NonNull Style loadedMapStyle) {

    // Check if permissions are enabled and if not request
    if (PermissionsManager.areLocationPermissionsGranted(context)) {

      /**
       * Want to customize the device location icon? Add the custom image to the drawables folder,
       * uncomment the code below, pass through the options
       * object in activateLocationComponent() below in place of the useDefaultLocationEngine boolean pass through.
       *
       * LocationComponentOptions locationComponentOptions = LocationComponentOptions.builder(context)
       *           .foregroundDrawable(R.drawable.CUSTOM_ICON_IMAGE_NAME)
       *           .foregroundDrawableStale(R.drawable.CUSTOM_ICON_IMAGE_NAME)
       *           .build();
       *
       * ...
       *
       * locationComponent.activateLocationComponent(context, loadedMapStyle, locationComponentOptions);
       **/

      // More information about the Maps SDK's LocationComponent: https://docs.mapbox.com/android/maps/overview/location-component/

      // Get the LocationComponent from the map
      locationComponent = mapboxMap.getLocationComponent();

      // Activate the LocationComponent. It can be customized if you pass in a
      // LocationComponentOptions object as method parameter (see code comments above)
      locationComponent.activateLocationComponent(context, loadedMapStyle, true);

      // Enable to make the LocationComponent's device icon visible on the map
      locationComponent.setLocationComponentEnabled(true);

      // Set the LocationComponent's camera mode
      locationComponent.setCameraMode(CameraMode.NONE);

      // Set the LocationComponent's render mode
      locationComponent.setRenderMode(RenderMode.NORMAL);
    } else {
      permissionsManager = new PermissionsManager(this);
      permissionsManager.requestLocationPermissions(context);
    }
  }

  /**
   * Adjusts the map text to match the device's set language. More info at
   * https://docs.mapbox.com/android/plugins/overview/localization/
   */
  private void enableLocalizationPlugin(@NonNull Style loadedMapStyle) {
    LocalizationPlugin localizationPlugin = new LocalizationPlugin(mapView, mapboxMap, loadedMapStyle);
    localizationPlugin.matchMapLanguageWithDeviceDefault();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  public void onPermissionResult(boolean granted) {
    if (granted && mapboxMap.getStyle() != null) {
      enableLocationComponent(mapboxMap.getStyle());
    } else {
      Toast.makeText(context, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
      context.finish();
    }
  }

  private void setVehicleCardview(int drawable, boolean scooterSelected) {
    ImageView vehicleTypeImageView = view.findViewById(R.id.vechicle_type_imageview);
    vehicleTypeImageView.setImageDrawable(getDrawable(context, drawable));
    view.findViewById(R.id.single_vehicle_distance_and_time_info_cardview).setVisibility(View.VISIBLE);
    TextView vehicleNumber = view.findViewById(R.id.vehicle_number);
    vehicleNumber.setText(String.format("#" + String.valueOf(new Random().nextInt(900))));
    Button rentVehicleButton = view.findViewById(R.id.start_turn_by_turn_navigate_to_bike_button);
    rentVehicleButton.setBackgroundColor(Color.parseColor(scooterSelected ? "#8045CA" : "#A2FCA2"));
    rentVehicleButton.setTextColor(Color.parseColor(scooterSelected ? "#FFFFFF" : "#000000"));
  }

  private void setMopedGarageCardview() {
    view.findViewById(R.id.single_moped_garage_location_info_cardview).setVisibility(View.VISIBLE);
    TextView vehicleNumber = view.findViewById(R.id.number_of_mopeds_in_garage_location);
    int mopedCount = new Random().nextInt(13);
    vehicleNumber.setText(String.format(mopedCount == 1 ? getString(R.string.one_moped) : getString(R.string.moped_count),
      mopedCount));
  }

  @Override
  @SuppressWarnings( {"MissingPermission"})
  public void onStart() {
    super.onStart();
    mapView.onStart();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == PLACE_SEARCH_REQUEST_CODE_AUTOCOMPLETE && resultCode == PLACE_SEARCH_RESULT_CODE_AUTOCOMPLETE) {

      // Retrieve selected location's CarmenFeature
      CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

      // Retrieve and update the source designated for showing a selected location's symbol layer icon
      if (mapboxMap.getStyle() != null) {
        GeoJsonSource source = mapboxMap.getStyle().getSourceAs(PLACES_PLUGIN_SEARCH_RESULT_SOURCE_ID);
        if (source != null) {
          source.setGeoJson(FeatureCollection.fromFeatures(
            new Feature[] {Feature.fromJson(selectedCarmenFeature.toJson())}));
        }

        // Move map camera to the selected location
        CameraPosition newCameraPosition = new CameraPosition.Builder()
          .target(new LatLng(selectedCarmenFeature.center().coordinates().get(1),
            selectedCarmenFeature.center().coordinates().get(0)))
          .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), 2000);
      }
    }
  }

  public boolean deviceHasInternetConnection() {
    ConnectivityManager connectivityManager = (ConnectivityManager)
      context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
    return activeNetwork != null && activeNetwork.isConnected();
  }

  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    mapView.onStop();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    mapView.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    // Remove the map click listener
    if (mapboxMap != null) {
      mapboxMap.removeOnMapClickListener(this);
    }
    // Remove the style loading listener
    if (mapView != null) {
      mapView.removeOnDidFinishLoadingStyleListener(this);
    }
    // Cancel the Directions API request
    if (directionsApiClient != null) {
      directionsApiClient.cancelCall();
    }
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }


  public VehicleMapFragment() {
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = (FragmentActivity) context;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      this.context = (FragmentActivity) activity;
    }
  }
}
