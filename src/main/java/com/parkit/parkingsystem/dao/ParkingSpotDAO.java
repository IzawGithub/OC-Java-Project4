package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.parkit.parkingsystem.config.AbstractDatabase;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.EVehicleType;
import com.parkit.parkingsystem.model.ParkingSpot;

public class ParkingSpotDAO {
    // Public

    public Integer getNextAvailableSlot(EVehicleType vehicleType) {
        Objects.requireNonNull(vehicleType);
        Integer result = null;
        try (PreparedStatement ps = db.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);) {
            ps.setString(1, vehicleType.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = Integer.valueOf(rs.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("Database: error fetching next available slot", e);
        }
        return result;
    }

    public Boolean updateParking(ParkingSpot parkingSpot) {
        Objects.requireNonNull(parkingSpot);
        Boolean result = null;
        try (PreparedStatement ps = db.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);) {
            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            int updateRowCount = ps.executeUpdate();
            result = Boolean.valueOf(updateRowCount == 1);
        } catch (Exception ex) {
            logger.error("Database: error updating parking info", ex);
        }
        return result;
    }

    // -- Ctors --

    public ParkingSpotDAO(AbstractDatabase configDB) {
        Objects.requireNonNull(configDB);
        db = configDB.getConnection();
    }

    // Private

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());
    private Connection db;
}
