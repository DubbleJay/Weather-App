package com.example.locationtest;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;

public class WeatherFetcher {

    private final String KEY = "6fdf142e83b58ab2831e15b6d04a59e2";
    private final String TAG = "WeatherFetcher";

    public WeatherObject fetchWeather(String city) throws IOException {

        WeatherObject weatherObject = new WeatherObject();

        try {

            String currentWeatherUrl = Uri.parse("https://api.openweathermap.org/data/2.5/weather")
                    .buildUpon()
                    .appendQueryParameter("q", city.trim())
                    .appendQueryParameter("appid", KEY)
                    .build().toString();
            String currentWeatherJsonString = new String(getJSon(currentWeatherUrl));
            JSONObject currentWeatherJsonBody = new JSONObject(currentWeatherJsonString);
            parseCurrentWeather(weatherObject, currentWeatherJsonBody);

            String oneCallWeatherUrl = Uri.parse("https://api.openweathermap.org/data/2.5/onecall")
                    .buildUpon()
                    .appendQueryParameter("lat", weatherObject.getLatitude() + "")
                    .appendQueryParameter("lon", weatherObject.getLongitude() + "")
                    .appendQueryParameter("appid", KEY)
                    .build().toString();
            String oneCallWeatherString = new String(getJSon(oneCallWeatherUrl));
            JSONObject oneCallWeatherJsonBody = new JSONObject(oneCallWeatherString);
            parseOneCall(weatherObject, oneCallWeatherJsonBody);

        } catch (JSONException jsonException) {
            Log.e(TAG, "Failed to parse JSON", jsonException);
        }

        return weatherObject;
    }

    public WeatherObject fetchWeather(String latitude, String longitude) throws IOException {

        WeatherObject weatherObject = new WeatherObject();

        try {

            String currentWeatherUrl = Uri.parse("https://api.openweathermap.org/data/2.5/weather")
                    .buildUpon()
                    .appendQueryParameter("lat", latitude)
                    .appendQueryParameter("lon", longitude)
                    .appendQueryParameter("appid", KEY)
                    .build().toString();
            String currentWeatherJsonString = new String(getJSon(currentWeatherUrl));
            JSONObject currentWeatherJsonBody = new JSONObject(currentWeatherJsonString);
            parseCurrentWeather(weatherObject, currentWeatherJsonBody);

            String oneCallWeatherUrl = Uri.parse("https://api.openweathermap.org/data/2.5/onecall")
                    .buildUpon()
                    .appendQueryParameter("lat", weatherObject.getLatitude() + "")
                    .appendQueryParameter("lon", weatherObject.getLongitude() + "")
                    .appendQueryParameter("appid", KEY)
                    .build().toString();
            String oneCallWeatherString = new String(getJSon(oneCallWeatherUrl));
            JSONObject oneCallWeatherJsonBody = new JSONObject(oneCallWeatherString);
            parseOneCall(weatherObject, oneCallWeatherJsonBody);

        }  catch (JSONException jsonException) {
            Log.e(TAG, "Failed to parse JSON", jsonException);

        }

        return weatherObject;
    }

    private byte[] getJSon (String urlSpec) throws IOException {

        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in  = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw  new IOException(connection.getResponseMessage() + ": with" + urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();

            return out.toByteArray();

        }

        finally {
            connection.disconnect();
        }

    }

    private void parseCurrentWeather(WeatherObject weatherObject, JSONObject jsonObject) throws JSONException {

        JSONObject coordinatesJsonObject = jsonObject.getJSONObject("coord");

        JSONObject weatherJsonObject = jsonObject.getJSONObject("main");

        JSONArray weatherArray= jsonObject.getJSONArray("weather");
        JSONObject weatherArrayJsonObject = weatherArray.getJSONObject(0);
        String description = weatherArrayJsonObject.getString("description");
        String icon = weatherArrayJsonObject.getString("icon");
        weatherObject.setTemperature(Double.parseDouble(weatherJsonObject.getString("temp")));
        weatherObject.setCityName(jsonObject.getString("name"));
        weatherObject.setDescription(description);
        weatherObject.setIconId(icon);
        weatherObject.setLatitude(coordinatesJsonObject.getDouble("lat"));
        weatherObject.setLongitude(coordinatesJsonObject.getDouble("lon"));

    }

    private void parseOneCall(WeatherObject weatherObject, JSONObject jsonObject) throws JSONException {

        String timeZone = jsonObject.getString("timezone");
        long timezone_offset = jsonObject.getLong("timezone_offset");

        JSONObject currentJsonObject = jsonObject.getJSONObject("current");
        long currentTime = currentJsonObject.getLong("dt");
        String dateAndTime = CurrentWeatherUtils.convertDate(currentTime, timezone_offset);
        weatherObject.setDateAndTimeString(dateAndTime);

        JSONArray dailyWeatherArray= jsonObject.getJSONArray("daily");
        JSONObject dailyWeatherArrayJsonObject = dailyWeatherArray.getJSONObject(0);
        JSONObject tempJsonObject = dailyWeatherArrayJsonObject.getJSONObject("temp");

        double dailyLow = tempJsonObject.getDouble("min");
        double dailyHigh = tempJsonObject.getDouble("max");

        weatherObject.setDailyLow(dailyLow);
        weatherObject.setDailyHigh(dailyHigh);

        weatherObject.setDailyForecast(getDailyForecast(dailyWeatherArray, timeZone));

        JSONArray hourlyWeatherArray = jsonObject.getJSONArray("hourly");
        weatherObject.setHourlyForecast(getHourlyForecast(hourlyWeatherArray, timeZone));

    }

    private ForecastedDay[] getDailyForecast(JSONArray dailyWeatherArray, String timeZone) throws JSONException {

        ForecastedDay[] forecastedDays = new ForecastedDay[8];

        for(int i = 0; i < dailyWeatherArray.length(); i++) {
            JSONObject dailyWeatherArrayJsonObject1 = dailyWeatherArray.getJSONObject(i);
            JSONObject tempJsonObject1 = dailyWeatherArrayJsonObject1.getJSONObject("temp");
            ForecastedDay forecastedDay = new ForecastedDay();
            forecastedDay.setDailyLow(tempJsonObject1.getDouble("min"));
            forecastedDay.setDailyHigh(tempJsonObject1.getDouble("max"));

            JSONArray weatherArray = dailyWeatherArrayJsonObject1.getJSONArray("weather");
            JSONObject weatherJsonObject = weatherArray.getJSONObject(0);
            forecastedDay.setDescription(weatherJsonObject.getString("description"));
            forecastedDay.setIcon(weatherJsonObject.getString("icon"));

            Calendar rightNow = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
            rightNow.add(Calendar.DAY_OF_MONTH, i);

            forecastedDay.setDate(CurrentWeatherUtils.getDate(rightNow.get(Calendar.DAY_OF_WEEK), rightNow.get(Calendar.MONTH), rightNow.get(Calendar.DAY_OF_MONTH)));
            forecastedDays[i] = forecastedDay;
        }

        return forecastedDays;
    }

    private ForecastedHour[] getHourlyForecast(JSONArray hourlyArray, String timeZone) throws JSONException {

        ForecastedHour[] forecastedHours = new ForecastedHour[48];

        for(int i = 0; i < hourlyArray.length(); i++) {
            ForecastedHour forecastedHour = new ForecastedHour();

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
            calendar.add(Calendar.HOUR, i);
            String amPMString  = calendar.get(Calendar.AM_PM) == Calendar.AM ? "am" : "pm";

            forecastedHour.setTimeString(calendar.get(Calendar.HOUR) != 0 ? calendar.get(Calendar.HOUR) + amPMString : 12 + amPMString);

            JSONObject hourlyArrayJsonObject = hourlyArray.getJSONObject(i);
            forecastedHour.setTemp(hourlyArrayJsonObject.getDouble("temp"));
            JSONArray hourlyWeatherArray = hourlyArrayJsonObject.getJSONArray("weather");
            JSONObject hourlyWeatherObject = hourlyWeatherArray.getJSONObject(0);
            forecastedHour.setDescription(hourlyWeatherObject.getString("description"));
            forecastedHour.setIcon(hourlyWeatherObject.getString("icon"));
            forecastedHours[i] = forecastedHour;
        }

        return forecastedHours;
    }

}
