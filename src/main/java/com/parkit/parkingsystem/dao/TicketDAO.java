package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.parkit.parkingsystem.config.AbstractDatabase;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.EVehicleType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {
    // Public

    public Ticket getTicket(String vehicleRegNumber) {
        Objects.requireNonNull(vehicleRegNumber);
        Ticket result = null;
        try (PreparedStatement ps = db.prepareStatement(DBConstants.GET_TICKET)) {
            Objects.requireNonNull(ps);
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                var ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), EVehicleType.valueOf(rs.getString(6)), false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4));
                ticket.setOutTime(rs.getTimestamp(5));
                result = ticket;
            }
        } catch (SQLException e) {
            logger.error("Database: error fetching the ticket", e);
        }
        return result;
    }

    public Boolean saveTicket(Ticket ticket) {
        Objects.requireNonNull(ticket);
        Boolean result = null;
        try(PreparedStatement ps = db.prepareStatement(DBConstants.SAVE_TICKET)) {
            // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            Objects.requireNonNull(ps);
            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            if (Objects.isNull(ticket.getOutTime()))
            {
                ps.setNull(5, java.sql.Types.NULL);
            }
            else {
                ps.setTimestamp(5, new Timestamp(ticket.getOutTime().getTime()));
            }
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Database: error saving the ticket", e);
        }
        return result;
    }

    public Boolean getTicketExitTimeNull(String vehicleRegNumber) {
        Objects.requireNonNull(vehicleRegNumber);
        Boolean result = null;
        try (PreparedStatement ps = db.prepareStatement(DBConstants.GET_TICKET_OUT)) {
            // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1, vehicleRegNumber);

            ResultSet rs = ps.executeQuery();
            result = Boolean.valueOf(rs.next());
        } catch (SQLException e) {
            logger.error("Database: error fetching ticket with null time", e);
        }
        return result;
    }

    public int getNbTicket(String vehicleRegNumber) {
        Objects.requireNonNull(vehicleRegNumber);
        int numberTicket = 0;
        var ticket = getTicket(vehicleRegNumber);
        if (ticket != null) {
            numberTicket++;
        }
        return numberTicket;
    }

    public Boolean updateTicket(Ticket ticket) {
        Objects.requireNonNull(ticket);
        Boolean result = null;
        try (PreparedStatement ps = db.prepareStatement(DBConstants.UPDATE_TICKET);) {
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3, ticket.getId());
            ps.execute();
            return true;
        } catch (SQLException e) {
            logger.error("Database: error updating the ticket information", e);
        }
        return result;
    }

    // -- Ctors --

    public TicketDAO(AbstractDatabase configDB) {
        Objects.requireNonNull(configDB);
        db = configDB.getConnection();
    }

    // Private

    // -- Interface --

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private Connection db;

}
