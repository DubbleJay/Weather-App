package com.example.locationtest;

public class WeatherObject {

    private String cityName;
    private String dateAndTimeString;
    private double temperature;
    private double dailyLow, dailyHigh;
    private String description;
    private String iconId;
    private double latitude, longitude;
    private ForecastedDay[] dailyForecast;
    private ForecastedHour[] hourlyForecast;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDateAndTimeString() {
        return dateAndTimeString;
    }

    public void setDateAndTimeString(String dateAndTimeString) {
        this.dateAndTimeString = dateAndTimeString;
    }

    public int getTemperature() {

        return (int) Math.round(temperature * 9 / 5 - 459.67);
    }

    public void setTemperature(double temperature) {

        this.temperature = temperature;
    }

    public int getDailyLow() {
        return (int) Math.round(dailyLow * 9 / 5 - 459.67);
    }

    public void setDailyLow(double dailyLow) {
        this.dailyLow = dailyLow;
    }

    public int getDailyHigh() {
        return (int) Math.round(dailyHigh * 9 / 5 - 459.67);
    }

    public void setDailyHigh(double dailyHigh) {
        this.dailyHigh = dailyHigh;
    }

    public String getDescription()
    {
        description = description.substring(0, 1).toUpperCase() + description.substring(1);
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ForecastedDay[] getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast(ForecastedDay[] dailyForecast) {
        this.dailyForecast = dailyForecast;
    }

    public ForecastedHour[] getHourlyForecast() {
        return hourlyForecast;
    }

    public void setHourlyForecast(ForecastedHour[] hourlyForecast) {
        this.hourlyForecast = hourlyForecast;
    }
}


