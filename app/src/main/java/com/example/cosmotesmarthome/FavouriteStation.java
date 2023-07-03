package com.example.cosmotesmarthome;


import java.io.Serializable;

public class FavouriteStation implements Serializable {
    private String stationName;
    private double stationFrequency;

    public FavouriteStation(String stationName, double stationFrequency) {
        this.stationName = stationName;
        this.stationFrequency = stationFrequency;

    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public double getStationFrequency() {
        return stationFrequency;
    }

    public void setStationFrequency(double stationFrequency) {
        this.stationFrequency = stationFrequency;
    }
}
