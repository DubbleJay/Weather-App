package com.example.locationtest;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CurrentWeatherUtils {

    public static String convertDate(long unixTimeStamp, long timeZoneOffset) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE, MMM d, h:mm aaa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date((unixTimeStamp + timeZoneOffset) *1000 );
        return dateFormat.format(date);
    }

    public static String getDate(int dayOfWeek, int month, int dayOfMonth) {

        String date = "";

        switch (dayOfWeek) {
            case 1:
                date = date + "Sunday ";
                break;

            case 2:
                date = date + "Monday ";
                break;

            case 3:
                date = date + "Tuesday ";
                break;


            case 4:
                date = date + "Wednesday ";
                break;

            case 5:
                date = date + "Thursday ";
                break;

            case 6:
                date = date + "Friday ";
                break;

            case 7:
                date = date + "Saturday ";
                break;
        }

        switch (month) {
            case 0:
                date = date + "January ";
                break;

            case 1:
                date = date + "February ";
                break;


            case 2:
                date = date + "March ";
                break;


            case 3:
                date = date + "April ";
                break;


            case 4:
                date = date + "May ";
                break;


            case 5:
                date = date + "June ";
                break;


            case 6:
                date = date + "July ";
                break;


            case 7:
                date = date + "August ";
                break;


            case 8:
                date = date + "September ";
                break;


            case 9:
                date = date + "October ";
                break;


            case 10:
                date = date + "November ";
                break;

            case 11:
                date = date + "December ";
                break;

        }

        date = date + dayOfMonth;

        return date;
    }

    public static int getIcon(final String iconId)  {
        switch (iconId) {
            case "01d":
                return R.drawable.ic__01d;

            case "01n":
                return R.drawable.ic__01n;

            case "02d":
                return R.drawable.ic__02d;

            case "02n":
                return R.drawable.ic__02n;

            case "03d":
                return R.drawable.ic__03d;

            case "03n":
                return R.drawable.ic__03n;

            case "04d":
                return R.drawable.ic__04d;

            case "04n":
                return R.drawable.ic__04n;

            case "09d":
                return R.drawable.ic__09d;

            case "09n":
                return R.drawable.ic__09n;

            case "10d":
                return R.drawable.ic__10d;

            case "10n":
                return R.drawable.ic__10n;

            case "11d":
                return R.drawable.ic__11d;

            case "11n":
                return R.drawable.ic__11n;

            case "13d":
                return R.drawable.ic__13d;

            case "13n":
                return R.drawable.ic__13n;

            case "50d":
                return R.drawable.ic__50d;

            case "50n":
                return R.drawable.ic__50n;

            default:
                return R.drawable.ic_wi_alien;
        }
    }
}