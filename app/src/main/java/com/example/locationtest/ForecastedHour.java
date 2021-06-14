package com.example.locationtest;

public class ForecastedHour {

    private String timeString;
    private double temp;
    private String description;
    private String icon;

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public int getTemp() {
        return (int) Math.round(temp * 9 / 5 - 459.67);

    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
