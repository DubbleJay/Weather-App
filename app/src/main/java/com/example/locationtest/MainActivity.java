package com.example.locationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.locationtest.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity  {

    private SearchView searchView;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult == null) {
                return;
            }

            for (Location location : locationResult.getLocations()) {
                Log.d("Location", "LocationResult: " + location.toString());

                new Thread(new FetchWeatherTask(location.getLatitude() + "",location.getLongitude() + "" )).start();
            }

            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        }
    };
    private String currentCity;
    private boolean usingCurrentLocation = true;
    private boolean locationButtonClicked = false;
    private static final String KEY_USING_CURRENT_LOCATION = "using_current_location";
    private static final String KEY_CITY_NAME = "city_name";
    private static final String TAG = "Weather";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
        binding.hourlyForecastRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {

            binding.swipeRefreshLayout.setRefreshing(false);

            if (usingCurrentLocation)
                getCurrentLocationWeather();

            else
                new Thread(new FetchWeatherTask(currentCity.trim())).start();

            binding.weatherContainer.setVisibility(View.GONE);
            searchView.setQuery("", false);
            searchView.setIconified(true);
            searchView.clearFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        });

        if (savedInstanceState != null) {

            usingCurrentLocation = savedInstanceState.getBoolean(KEY_USING_CURRENT_LOCATION);
            currentCity = savedInstanceState.getString(KEY_CITY_NAME);

            showWeatherRequestUi();

            if (usingCurrentLocation)
                getCurrentLocationWeather();

            else
                new Thread(new FetchWeatherTask(currentCity.trim())).start();

        }

        else {
            if (checkLocationPermission())
                getCurrentLocationWeather();
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }

            return false;
        } else { //permission is already granted
            return true;
            // start to find location...
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    getCurrentLocationWeather();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (!locationButtonClicked) {
                        usingCurrentLocation = false;
                        showWeatherRequestUi();
                        new Thread(new FetchWeatherTask("Baghdad")).start();
                    }

                    else {
                        locationButtonClicked = false;
                        return;
                    }
                }
                return;
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_USING_CURRENT_LOCATION, usingCurrentLocation);
        outState.putString(KEY_CITY_NAME, currentCity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchView.clearFocus();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                showWeatherRequestUi();

                usingCurrentLocation = false;

                new Thread(new FetchWeatherTask(s.trim())).start();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_current_location) {

                if(checkLocationPermission())
                    getCurrentLocationWeather();

                else {
                    locationButtonClicked = true;

                    new AlertDialog.Builder(this)
                            .setTitle("Location Permission")
                            .setMessage("Weather needs your permission to get the forecast for your current location,")

                            .setPositiveButton("Open Settings", (dialogInterface, i) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", (dialogInterface, i) -> {

                            })
                            .create()
                            .show();
                }
                
        }
        return true;
    }

    private void showWeatherRequestUi() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.weatherContainer.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocationWeather() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        usingCurrentLocation = true;
        showWeatherRequestUi();
    }

    private class FetchWeatherTask implements Runnable {

        private String city;
        private String lat, lon;
        private WeatherObject weatherObject;

        FetchWeatherTask(String city) {
            this.city = city;
        }

        FetchWeatherTask(String lat, String lon) {
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        public void run() {

            try {

                WeatherFetcher weatherFetcher = new WeatherFetcher();

                if (lat != null)
                    weatherObject = weatherFetcher.fetchWeather(lat, lon);

                else
                    weatherObject = weatherFetcher.fetchWeather(city);

            } catch (IOException ioException) {

                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.errorTextView.setVisibility(View.VISIBLE);
                });

                Log.e(TAG, "Failed to fetch URL " + ioException);
                return;
            }

            runOnUiThread(() -> {
                currentCity = weatherObject.getCityName();
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.errorTextView.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.weatherContainer.setVisibility(View.VISIBLE);
                binding.timeAndDateTextView.setText(weatherObject.getDateAndTimeString());
                binding.currentTempTextView.setText(weatherObject.getTemperature() + "°");
                binding.cityTextView.setText(weatherObject.getCityName());
                binding.descriptionTextView.setText(weatherObject.getDescription());
                binding.currentWeatherImageView.setImageResource(WeatherUtils.getIcon(weatherObject.getIconId()));
                binding.highAndLowTempsTextView.setText("High: ↑ " + weatherObject.getDailyHigh() + "°" + " Low: ↓ "
                        + weatherObject.getDailyLow() + "°");
                binding.dailyForecastLinearLayout.removeAllViews();
                for (ForecastedDay forecastedDay : weatherObject.getDailyForecast())
                    binding.dailyForecastLinearLayout.addView(new DailyForecastView(getApplicationContext(), forecastedDay));
                binding.hourlyForecastRecyclerView.setAdapter(new HourlyForecastAdapter(weatherObject.getHourlyForecast()));
            });
        }
    }

    private static class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastHolder> {

        private final ForecastedHour[] hours;

        HourlyForecastAdapter(ForecastedHour[] hours) {
            this.hours = hours;
        }

        @NonNull
        @Override
        public HourlyForecastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_forecasted_hour, parent, false);
            return new HourlyForecastHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull HourlyForecastHolder holder, int position) {
            ForecastedHour forecastedHour = hours[position];
            holder.bindForecastedHour(forecastedHour);
        }

        @Override
        public int getItemCount() {
            return hours.length;
        }
    }

    private static class HourlyForecastHolder extends RecyclerView.ViewHolder {

        private final TextView timeTextView;
        private final ImageView hourlyImageView;
        private final TextView hourlyTempTextView;

        HourlyForecastHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            hourlyImageView = itemView.findViewById(R.id.hourlyImageView);
            hourlyTempTextView = itemView.findViewById(R.id.hourlyTempTextView);
        }

        public void bindForecastedHour(ForecastedHour forecastedHour) {
            timeTextView.setText(forecastedHour.getTimeString());
            hourlyImageView.setImageResource(WeatherUtils.getIcon(forecastedHour.getIcon()));
            hourlyTempTextView.setText(forecastedHour.getTemp() + "°");
        }
    }

    private static class DailyForecastView extends RelativeLayout {

        public DailyForecastView(Context context, ForecastedDay forecastedDay)  {
            super(context);
            inflate(context, R.layout.list_item_forecasted_day, this);
            TextView dayOfTheWeekTextView = findViewById(R.id.day_of_the_week_text_view);
            dayOfTheWeekTextView.setText(forecastedDay.getDate());
            TextView descriptionTextView = findViewById(R.id.forecasted_day_description_text_view);
            descriptionTextView.setText(forecastedDay.getDescription());
            ImageView imageView = findViewById(R.id.forecasted_day_image_view);
            imageView.setImageResource(WeatherUtils.getIcon(forecastedDay.getIcon()));
            TextView highTempTextView = findViewById(R.id.forecasted_high_text_view);
            highTempTextView.setText("↑ " + forecastedDay.getDailyHigh() +  "°");
            TextView lowTempTextView = findViewById(R.id.forecasted_low_text_view);
            lowTempTextView.setText("↓ "+ forecastedDay.getDailyLow() +  "°");
        }
    }
}