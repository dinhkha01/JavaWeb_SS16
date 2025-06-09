package dk.model.dao.bt1;

import dk.configs.ConnectionDB;
import dk.model.entity.bt1.Trip;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Repository
public class TripDao {

    // Tạo bảng trips nếu chưa tồn tại và thêm dữ liệu mẫu
    public void createTripTableIfNotExists() {
        try (Connection conn = ConnectionDB.openConnection();
             Statement stmt = conn.createStatement()) {

            // Tạo bảng
            stmt.execute("CREATE TABLE IF NOT EXISTS trips (" +
                    "id VARCHAR(36) PRIMARY KEY," +
                    "flight_number VARCHAR(20) UNIQUE NOT NULL," +
                    "departure VARCHAR(100) NOT NULL," +
                    "destination VARCHAR(100) NOT NULL," +
                    "departure_time TIMESTAMP NOT NULL," +
                    "arrival_time TIMESTAMP NOT NULL," +
                    "price DECIMAL(10,2) NOT NULL," +
                    "available_seats INT NOT NULL," +
                    "total_seats INT NOT NULL," +
                    "status VARCHAR(20) DEFAULT 'ACTIVE'," +
                    "airline VARCHAR(100) NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")");

            // Kiểm tra và thêm dữ liệu mẫu nếu bảng trống
            if (getTripsCount() == 0) {
                insertSampleData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Lấy tổng số chuyến bay
    public int getTripsCount() {
        String sql = "SELECT COUNT(*) FROM trips WHERE status = 'ACTIVE'";
        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy tổng số chuyến bay theo điều kiện tìm kiếm
    public int getTripsCount(String departure, String destination) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM trips WHERE status = 'ACTIVE'");

        if (departure != null && !departure.trim().isEmpty()) {
            sql.append(" AND LOWER(departure) LIKE LOWER(?)");
        }
        if (destination != null && !destination.trim().isEmpty()) {
            sql.append(" AND LOWER(destination) LIKE LOWER(?)");
        }

        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (departure != null && !departure.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + departure + "%");
            }
            if (destination != null && !destination.trim().isEmpty()) {
                pstmt.setString(paramIndex, "%" + destination + "%");
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy danh sách chuyến bay với phân trang
    public List<Trip> getTrips(int offset, int limit) {
        createTripTableIfNotExists();

        String sql = "SELECT * FROM trips WHERE status = 'ACTIVE' " +
                "ORDER BY departure_time ASC LIMIT ? OFFSET ?";

        List<Trip> trips = new ArrayList<>();

        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getString("id"));
                trip.setFlightNumber(rs.getString("flight_number"));
                trip.setDeparture(rs.getString("departure"));
                trip.setDestination(rs.getString("destination"));
                trip.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                trip.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
                trip.setPrice(rs.getBigDecimal("price"));
                trip.setAvailableSeats(rs.getInt("available_seats"));
                trip.setTotalSeats(rs.getInt("total_seats"));
                trip.setStatus(rs.getString("status"));
                trip.setAirline(rs.getString("airline"));

                trips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trips;
    }

    // Tìm kiếm chuyến bay với phân trang
    public List<Trip> searchTrips(String departure, String destination, int offset, int limit) {
        StringBuilder sql = new StringBuilder("SELECT * FROM trips WHERE status = 'ACTIVE'");

        if (departure != null && !departure.trim().isEmpty()) {
            sql.append(" AND LOWER(departure) LIKE LOWER(?)");
        }
        if (destination != null && !destination.trim().isEmpty()) {
            sql.append(" AND LOWER(destination) LIKE LOWER(?)");
        }

        sql.append(" ORDER BY departure_time ASC LIMIT ? OFFSET ?");

        List<Trip> trips = new ArrayList<>();

        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (departure != null && !departure.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + departure + "%");
            }
            if (destination != null && !destination.trim().isEmpty()) {
                pstmt.setString(paramIndex++, "%" + destination + "%");
            }

            pstmt.setInt(paramIndex++, limit);
            pstmt.setInt(paramIndex, offset);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Trip trip = new Trip();
                trip.setId(rs.getString("id"));
                trip.setFlightNumber(rs.getString("flight_number"));
                trip.setDeparture(rs.getString("departure"));
                trip.setDestination(rs.getString("destination"));
                trip.setDepartureTime(rs.getTimestamp("departure_time").toLocalDateTime());
                trip.setArrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime());
                trip.setPrice(rs.getBigDecimal("price"));
                trip.setAvailableSeats(rs.getInt("available_seats"));
                trip.setTotalSeats(rs.getInt("total_seats"));
                trip.setStatus(rs.getString("status"));
                trip.setAirline(rs.getString("airline"));

                trips.add(trip);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return trips;
    }

    // Thêm dữ liệu mẫu
    private void insertSampleData() {
        String sql = "INSERT INTO trips (id, flight_number, departure, destination, departure_time, arrival_time, price, available_seats, total_seats, airline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Dữ liệu mẫu
            Object[][] sampleData = {
                    {"VN101", "Hà Nội", "TP.HCM", "2024-12-15 08:00:00", "2024-12-15 10:00:00", 2500000, 45, 180, "Vietnam Airlines"},
                    {"VJ201", "TP.HCM", "Đà Nẵng", "2024-12-15 14:30:00", "2024-12-15 16:00:00", 1800000, 32, 150, "VietJet Air"},
                    {"BB301", "Đà Nẵng", "Hà Nội", "2024-12-16 09:15:00", "2024-12-16 11:30:00", 2200000, 28, 120, "Bamboo Airways"},
                    {"VN102", "Hà Nội", "Cần Thơ", "2024-12-16 07:45:00", "2024-12-16 09:45:00", 2100000, 67, 180, "Vietnam Airlines"},
                    {"VJ202", "TP.HCM", "Phú Quốc", "2024-12-17 16:20:00", "2024-12-17 17:30:00", 1600000, 89, 150, "VietJet Air"},
                    {"BB302", "Nha Trang", "Hà Nội", "2024-12-17 11:00:00", "2024-12-17 13:15:00", 2400000, 23, 120, "Bamboo Airways"},
                    {"VN103", "Hà Nội", "Huế", "2024-12-18 06:30:00", "2024-12-18 08:00:00", 1900000, 56, 180, "Vietnam Airlines"},
                    {"VJ203", "Đà Lạt", "TP.HCM", "2024-12-18 13:45:00", "2024-12-18 15:00:00", 1400000, 78, 150, "VietJet Air"},
                    {"BB303", "Quy Nhon", "Hà Nội", "2024-12-19 10:30:00", "2024-12-19 12:45:00", 2000000, 41, 120, "Bamboo Airways"},
                    {"VN104", "TP.HCM", "Hải Phòng", "2024-12-19 15:15:00", "2024-12-19 17:30:00", 2300000, 34, 180, "Vietnam Airlines"},
                    {"VJ204", "Hà Nội", "Vinh", "2024-12-20 08:45:00", "2024-12-20 10:00:00", 1700000, 62, 150, "VietJet Air"},
                    {"BB304", "Pleiku", "TP.HCM", "2024-12-20 12:00:00", "2024-12-20 13:30:00", 1800000, 37, 120, "Bamboo Airways"}
            };

            for (Object[] data : sampleData) {
                pstmt.setString(1, UUID.randomUUID().toString());
                pstmt.setString(2, (String) data[0]);
                pstmt.setString(3, (String) data[1]);
                pstmt.setString(4, (String) data[2]);
                pstmt.setTimestamp(5, Timestamp.valueOf((String) data[3]));
                pstmt.setTimestamp(6, Timestamp.valueOf((String) data[4]));
                pstmt.setBigDecimal(7, new BigDecimal((Integer) data[5]));
                pstmt.setInt(8, (Integer) data[6]);
                pstmt.setInt(9, (Integer) data[7]);
                pstmt.setString(10, (String) data[8]);

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}