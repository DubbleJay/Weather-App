package com.example.locationtest;

public class ForecastedDay {

    private String date;
    private double dailyLow, dailyHigh;
    private String description;
    private String icon;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDailyHigh() {

        return (int) Math.round(dailyHigh * 9 / 5 - 459.67);
    }

    public int getDailyLow() {
        return (int) Math.round(dailyLow * 9 / 5 - 459.67);
    }

    public void setDailyLow(double dailyLow) {
        this.dailyLow = dailyLow;
    }

    public void setDailyHigh(double dailyHigh) {
        this.dailyHigh = dailyHigh;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description.substring(0, 1).toUpperCase() + description.substring(1);
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
