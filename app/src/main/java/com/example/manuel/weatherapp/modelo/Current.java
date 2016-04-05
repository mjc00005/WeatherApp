package com.example.manuel.weatherapp.modelo;

import com.example.manuel.weatherapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Manuel on 22/03/2016.
 */
public class Current {
    private String mIcon;
    private long mTime;
    private double mTemparature;
    private double mHumidity;
    private double mPrecipiChance;
    private String mSummary;
    private String mTimeZone;

    public Current() {
    }

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public String getIcon() {
        return mIcon;
    }

    public int getIconId(){
        return Forecast.getIconId(mIcon);
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getPrecipiChance() {
        double precipPercentage = mPrecipiChance*100;

        return (int)Math.round(precipPercentage);
    }

    public void setPrecipiChance(double precipiChance) {
        mPrecipiChance = precipiChance;
    }

    public String getSummary() {
        return mSummary;
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemparature() {
        float tempCelsius;
        float aux = (float)5/9;
        tempCelsius = (float) ((mTemparature-32)*aux );

        return (int)Math.round(tempCelsius);
    }

    public void setTemparature(double temparature) {
        mTemparature = temparature;
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));

        //Mirar la APi documentacion si son milisegundos o no
        Date dateTime = new Date(getTime()*1000);
        String timeString = formatter.format(dateTime);

        return timeString;
    }

    public void setTime(long time) {
        mTime = time;
    }


    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }
}
