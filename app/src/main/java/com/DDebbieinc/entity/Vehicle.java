package com.DDebbieinc.entity;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by appsplanet on 23/2/16.
 */
public class Vehicle {
    private String vehicleType;
    private String latitude;
    private String longitude;
    private String distance;
    private String name;
    private String photo;

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    private String model;
    private String driverId;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String number;
    private Marker marker;
    private String id;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Vehicle(String id, String driverId,String vehicleType, String latitude, String longitude,
                   String name, String photo, String model, String number) {
        this.vehicleType = vehicleType;
        this.latitude = latitude;
        this.longitude = longitude;
        //this.distance = distance;
        this.name = name;
        this.id = id;
        this.photo = photo;
        this.model = model;
        this.number = number;
        this.driverId = driverId;
    }

    public String getVehicleType() {

        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
