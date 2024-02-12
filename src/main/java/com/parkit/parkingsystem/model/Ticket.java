package com.parkit.parkingsystem.model;

import java.util.Date;

public class Ticket {
    private int id;
    private ParkingSpot parkingSpot;
    private String vehicleRegNumber;
    private double price;
    private Date inTime;
    private Date outTime;

    public int getId() {
        return id;
    }

    public Ticket setId(int id) {
        this.id = id;
        return this;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public Ticket setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
        return this;
    }

    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    public Ticket setVehicleRegNumber(String vehicleRegNumber) {
        this.vehicleRegNumber = vehicleRegNumber;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Ticket setPrice(double price) {
        this.price = price;
        return this;
    }

    public Date getInTime() {
        return inTime;
    }

    public Ticket setInTime(Date inTime) {
        this.inTime = inTime;
        return this;
    }

    public Date getOutTime() {
        return outTime;
    }

    public Ticket setOutTime(Date outTime) {
        this.outTime = outTime;
        return this;
    }
}
