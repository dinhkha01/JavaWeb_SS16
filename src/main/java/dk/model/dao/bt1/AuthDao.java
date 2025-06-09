package dk.model.dao.bt1;

import dk.configs.ConnectionDB;
import dk.model.entity.bt1.User;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.UUID;

@Repository
public class AuthDao {

    // Tạo bảng user nếu chưa tồn tại
    public void createUserTableIfNotExists() {
        try (Connection conn = ConnectionDB.openConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(" CREATE TABLE IF NOT EXISTS users (\n" +
                    "                id VARCHAR(36) PRIMARY KEY,\n" +
                    "                username VARCHAR(50) UNIQUE NOT NULL,\n" +
                    "                password VARCHAR(255) NOT NULL,\n" +
                    "                email VARCHAR(100) UNIQUE NOT NULL,\n" +
                    "                role VARCHAR(20) DEFAULT 'USER',\n" +
                    "                status VARCHAR(20) DEFAULT 'ACTIVE',\n" +
                    "                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    "            )");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Đăng ký người dùng mới
    public boolean registerUser(User user) {
        createUserTableIfNotExists();

        String sql = "INSERT INTO users (id, username, password, email, role, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole());
            pstmt.setString(6, user.getStatus());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra email đã tồn tại
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đăng nhập - xác thực user
    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 'ACTIVE'";

        try (Connection conn = ConnectionDB.openConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getString("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}