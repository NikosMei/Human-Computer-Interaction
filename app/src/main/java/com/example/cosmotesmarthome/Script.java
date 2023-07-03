package com.example.cosmotesmarthome;

import java.io.Serializable;
import java.util.HashMap;

public class Script implements Serializable {
    private HashMap<Double,String> favouriteRadioStations = new HashMap<>();
    private HashMap<String,Boolean> roomsLighting = new HashMap<>();

    private HashMap<String,Boolean> roomLocks = new HashMap<>();
    private boolean heatingSetting; //0 for cold 1 for heat

    public HashMap<Double, String> getFavouriteRadioStations() {
        return favouriteRadioStations;
    }

    public HashMap<String, Boolean> getRoomsLighting() {
        return roomsLighting;
    }

    public HashMap<String, Boolean> getRoomLocks() {
        return roomLocks;
    }

    public boolean isHeatingSetting() {
        return heatingSetting;
    }

    public boolean isHeatingOpen() {
        return heatingOpen;
    }

    public int getHeatingTemperature() {
        return heatingTemperature;
    }

    public double getLastStation() {
        return lastStation;
    }

    private boolean heatingOpen; //0 for closed 1 for open
    private int heatingTemperature;

    private double lastStation;

    public Script(HashMap<Double,String> favouriteRadioStations, HashMap<String,Boolean> roomsLighting, HashMap<String, Boolean> roomLocks, boolean heatingSetting, boolean heatingOpen, int heatingTemperature, double lastStation) {
        this.favouriteRadioStations = favouriteRadioStations;
        this.roomsLighting = roomsLighting;
        this.roomLocks = roomLocks;
        this.heatingSetting = heatingSetting;
        this.heatingOpen = heatingOpen;
        this.heatingTemperature = heatingTemperature;
        this.lastStation = lastStation;
    }
}
