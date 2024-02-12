package com.parkit.parkingsystem.service;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.parkit.parkingsystem.config.AbstractDatabase;
import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.util.InputReaderUtil;

public class InteractiveShell {
    public void loadInterface() {
        logger.debug("App initialized!!!");
        logger.info("Welcome to Parking System!");

        boolean continueApp = true;
        while (continueApp) {
            continueApp = mainLoop();
        }
    }

    // -- Ctors --

    public InteractiveShell(AbstractDatabase db) {
        Objects.requireNonNull(db);
        parkingSpotDAO = new ParkingSpotDAO(db);
        ticketDAO = new TicketDAO(db);
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    public InteractiveShell() throws IOException {
        this(new DataBaseConfig());
    }

    InteractiveShell(InputReaderUtil reader, ParkingSpotDAO parking, TicketDAO ticket, ParkingService service) {
        Objects.requireNonNull(reader);
        Objects.requireNonNull(parking);
        Objects.requireNonNull(ticket);
        Objects.requireNonNull(service);
        inputReaderUtil = reader;
        parkingSpotDAO = parking;
        ticketDAO = ticket;
        parkingService = service;
    }

    // Private

    // -- Interfaces --

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private InputReaderUtil inputReaderUtil = new InputReaderUtil();
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;
    private ParkingService parkingService;

    // -- Functions --

    boolean mainLoop() {
        printMenu();
        var option = inputReaderUtil.readUserInputAsInt();
        switch (option) {
            case 1: {
                logger.debug("Case '{}': Processing incoming vehicle", option);
                parkingService.processIncomingVehicle();
                break;
            }
            case 2: {
                logger.debug("Case '{}': Processing exiting vehicle", option);
                parkingService.processExitingVehicle();
                break;
            }
            case 3: {
                logger.debug("Case '{}': Exiting", option);
                logger.info("Exiting from the system!");
                return false;
            }
            default:
                logger.warn("Unsupported option. Please enter a number corresponding to the provided menu");
        }
        return true;
    }

    private void printMenu() {
        logger.info("Please select an option. Simply enter the number to choose an action");
        logger.info("1 New Vehicle Entering - Allocate Parking Space");
        logger.info("2 Vehicle Exiting - Generate Ticket Price");
        logger.info("3 Shutdown System");
    }
}
