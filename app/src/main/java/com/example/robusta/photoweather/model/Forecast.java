package com.example.robusta.photoweather.model;

/**
 * Created by robusta on 8/26/18.
 */

public class Forecast {
    private long time;
    private String summary;
    private String icon;
    private float temperature;
    private float apparentTemperature;

    public Forecast() {
    }

    public Forecast(long time, String summary, String icon, float temperature, float apparentTemperature) {
        this.time = time;
        this.summary = summary;
        this.icon = icon;
        this.temperature = temperature;
        this.apparentTemperature = apparentTemperature;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getApparentTemperature() {
        return apparentTemperature;
    }

    public void setApparentTemperature(float apparentTemperature) {
        this.apparentTemperature = apparentTemperature;
    }
}
