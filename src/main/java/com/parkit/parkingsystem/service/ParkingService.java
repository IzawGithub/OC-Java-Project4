package com.parkit.parkingsystem.service;

import java.util.Date;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.parkit.parkingsystem.constants.EVehicleType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class ParkingService {
    // Public

    public void processIncomingVehicle() {
        final ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
        if (Objects.isNull(parkingSpot)) {
            logger.debug("ParkingSpot is null");
            return;
        }
        if (parkingSpot.getId() <= 0) {
            logger.error("Error: ID of the parking spot is less than or equal to 0. Parking might be full");
            return;
        }
        final String vehicleRegNumber = getVehicleRegNumber();
        if (Objects.isNull(vehicleRegNumber)) {
            logger.debug("Vehicle registration number is null");
            return;
        }
        if (ticketDAO.getNbTicket(vehicleRegNumber) > 0) {
            final var ticketDidntExitYet = ticketDAO.getTicketExitTimeNull(vehicleRegNumber);
            if (Boolean.TRUE.equals(ticketDidntExitYet)) {
                logger.error(
                        "Error: tried to save vehicle registration number '{}' for a vehicle that is already inside the car park",
                        vehicleRegNumber);
                return;
            }
            logger.info("""
                    Heureux de vous revoir !
                    En tant qu'utilisateur régulier de notre parking, vous allez obtenir une remise de 5%.""");
        }
        parkingSpot.setAvailable(false);
        final var updatedParking = parkingSpotDAO.updateParking(parkingSpot);
        if (Objects.isNull(updatedParking) || Boolean.FALSE.equals(updatedParking)) {
            logger.error("Error: Could not update the parking spot succesfully");
            return;
        }

        Date inTime = new Date();
        Ticket ticket = new Ticket();
        // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
        ticket.setParkingSpot(parkingSpot)
                .setVehicleRegNumber(vehicleRegNumber)
                .setPrice(0)
                .setInTime(inTime)
                .setOutTime(null);
        final var savedTicket = ticketDAO.saveTicket(ticket);
        if (Objects.isNull(savedTicket) || Boolean.FALSE.equals(savedTicket)) {
            logger.error("Error: could not save the ticket to the parking succesfully");
            return;
        }
        logger.info("Generated Ticket and saved in DB");
        logger.info("Please park your vehicle in spot number: '{}'", parkingSpot.getId());
        logger.info("Recorded in-time for vehicle number: '{}' is: '{}'",
                vehicleRegNumber, inTime);

    }

    public void processExitingVehicle() {
        String vehicleRegNumber = getVehicleRegNumber();
        if (Objects.isNull(vehicleRegNumber)) {
            logger.debug("Vehicle registration number is null");
            return;
        }
        Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
        if (Objects.isNull(ticket)) {
            logger.error("Error: The registration number given does not match with a vehicle in the car park");
            return;
        }
        if (!Objects.isNull(ticket.getOutTime()))
        {
            logger.error("Error: cannot exit a vehicle that already left the carpark");
            return;
        }

        Date outTime = new Date();
        ticket.setOutTime(outTime);
        boolean applyDiscount = false;
        if (ticketDAO.getNbTicket(vehicleRegNumber) > 0) {
            applyDiscount = true;
        }

        ticket = FareCalculatorService.calculateFare(ticket, applyDiscount);
        if (Objects.isNull(ticket)) {
            logger.error("Error: calculation of the exit ticket price returned Not a Number");
            return;
        }
        if (Boolean.TRUE.equals(ticketDAO.updateTicket(ticket))) {
            ParkingSpot parkingSpot = ticket.getParkingSpot();
            parkingSpot.setAvailable(true);
            parkingSpotDAO.updateParking(parkingSpot);
            logger.info("Vehicle exit succesfull");
            logger.info("Please pay the parking fare: '{}'€", ticket.getPrice());
            logger.info(
                    "Recorded out-time for vehicle number: '{}' is: '{}'", ticket.getVehicleRegNumber(),
                    outTime);
        } else {
            logger.error("Error: unable to update ticket information");
        }

    }

    // -- Ctors --

    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO) {
        Objects.requireNonNull(inputReaderUtil);
        Objects.requireNonNull(parkingSpotDAO);
        Objects.requireNonNull(ticketDAO);
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    // Private

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;

    // -- Private functions --

    private String getVehicleRegNumber() {
        logger.info("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    private ParkingSpot getNextParkingNumberIfAvailable() {
        ParkingSpot result = null;
        var vehicleType = askUserForVehicleType();
        if (Objects.isNull(vehicleType))
        {
            logger.debug("Vehicle registration number is null");
            return result;
        }
        var parkingNumber = parkingSpotDAO.getNextAvailableSlot(vehicleType);
        if (!Objects.isNull(parkingNumber)) {
            result = new ParkingSpot(parkingNumber, vehicleType, true);
        } else {
            logger.debug("Parking number is null");
        }
        return result;
    }

    private EVehicleType askUserForVehicleType() {
        logger.info("Please select vehicle type from menu: \n\t{}: '{}'\n\t{}: '{}'",
                EVehicleType.CAR.getValue(), EVehicleType.CAR.name(), EVehicleType.BIKE.getValue(),
                EVehicleType.BIKE.name());
        EVehicleType vehicleType = null;
        final var input = inputReaderUtil.readUserInputAsInt();
        for (final var eValue : EVehicleType.values()) {
            if (eValue.getValue() == input) {
                vehicleType = eValue;
            }
        }
        if (Objects.isNull(vehicleType)) {
            logger.error("Error: input '{}' is not a valid vehicle type", input);
        }
        return vehicleType;
    }
}
