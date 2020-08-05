package com.mapbox.mapboxgoshare;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.listeners.NavigationListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.api.directions.v5.DirectionsCriteria.PROFILE_WALKING;

/**
 * This fragment shows the Mapbox Navigation SDK for Android's turn-by-turn directions capability.
 * The directions guide a user from the device's location to a selected scooter/bike.
 */
public class TurnByTurnNavigationFragment extends android.app.Fragment
  implements OnNavigationReadyCallback, NavigationListener {

  public Double selectedDestinationLat;
  public Double selectedDestinationLong;
  public Double selectedOriginLat;
  public Double selectedOriginLong;

  private NavigationView navigationView;
  private Context context;

  public TurnByTurnNavigationFragment() {
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      this.context = activity;
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    if (getArguments() != null) {
      selectedDestinationLat = getArguments().getDouble("NAVIGATION_DESTINATION_LAT");
      selectedDestinationLong = getArguments().getDouble("NAVIGATION_DESTINATION_LONG");
      selectedOriginLat = getArguments().getDouble("NAVIGATION_ORIGIN_LAT");
      selectedOriginLong = getArguments().getDouble("NAVIGATION_ORIGIN_LONG");
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.nav_fragment_layout, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    navigationView = view.findViewById(R.id.turn_by_turn_navigationView);
    navigationView.onCreate(savedInstanceState);
    navigationView.initialize(this);
  }

  @Override
  public void onNavigationReady(boolean isRunning) {
    NavigationRoute.builder(context)
      .accessToken(getString(R.string.mapbox_access_token))
      .origin(Point.fromLngLat(selectedOriginLong, selectedOriginLat))
      .destination(Point.fromLngLat(selectedDestinationLong, selectedDestinationLat))
      .profile(PROFILE_WALKING)
      .build()
      .getRoute(new Callback<DirectionsResponse>() {
        @Override
        public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
          NavigationViewOptions options = NavigationViewOptions.builder()
            .navigationListener(TurnByTurnNavigationFragment.this)
            .directionsRoute(response.body().routes().get(0))
            .shouldSimulateRoute(true)
              .build();
          navigationView.startNavigation(options);
        }

        @Override
        public void onFailure(Call<DirectionsResponse> call, Throwable t) {
          Toast.makeText(context, R.string.failure_to_retrieve, Toast.LENGTH_LONG).show();
        }
      });
  }

  @Override
  public void onStart() {
    super.onStart();
    navigationView.onStart();
  }

  @Override
  public void onResume() {
    super.onResume();
    navigationView.onResume();
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    navigationView.onSaveInstanceState(outState);
    super.onSaveInstanceState(outState);
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    if (savedInstanceState != null) {
      navigationView.onRestoreInstanceState(savedInstanceState);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    navigationView.onPause();
  }

  @Override
  public void onStop() {
    super.onStop();
    navigationView.onStop();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    navigationView.onLowMemory();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    navigationView.onDestroy();
  }

  @Override
  public void onCancelNavigation() {
    goBackToVehicleMapFragment();
  }

  @Override
  public void onNavigationFinished() {
    goBackToVehicleMapFragment();
  }

  @Override
  public void onNavigationRunning() {
  }

  private void goBackToVehicleMapFragment() {
    VehicleMapFragment newFragment = new VehicleMapFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.map_fragment_container, newFragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }
}
