package com.parkit.parkingsystem.service;

import java.util.concurrent.TimeUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    public void calculateFare(Ticket ticket, boolean discountRecurringUser) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inMinutes = TimeUnit.MILLISECONDS.toMinutes(ticket.getInTime().getTime());
        long outMinutes = TimeUnit.MILLISECONDS.toMinutes(ticket.getOutTime().getTime());

        double hourDuration = (double) (outMinutes - inMinutes) / TimeUnit.HOURS.toMinutes(1);

        if (isFreeParking(hourDuration)) {
            ticket.setPrice(0);
            return;
        }

        // Discount logic
        double discount = 1;
        if (discountRecurringUser) {
            discount = 0.95;
        }

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(hourDuration * Fare.CAR_RATE_PER_HOUR * discount);
                break;
            }
            case BIKE: {
                ticket.setPrice(hourDuration * Fare.BIKE_RATE_PER_HOUR * discount);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }

    private boolean isFreeParking(double hourDuration) {
        // Free parking if the user stayed less than 30m
        if (hourDuration < 0.5) {
            return true;
        }
        return false;
    }
}
