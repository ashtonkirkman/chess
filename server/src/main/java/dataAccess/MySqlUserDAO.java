package dataAccess;

import com.google.gson.Gson;
import model.UserData;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import static java.sql.Types.NULL;

public class MySqlUserDAO implements UserDAO {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(UserData user) throws DataAccessException {
        if (usernameExists(user.username())) {
            throw new DataAccessException("Error: Username already exists");
        }
        if (emailExists(user.email())) {
            throw new DataAccessException("Error: Email already in use");
        }
        String hashedPassword = encodePassword(user.password());
        var statement = "INSERT INTO user_data (username, password, email) VALUES(?,?,?)";
        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    public boolean emailExists(String email) throws DataAccessException {
        var statement = "SELECT email FROM user_data WHERE email = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, email);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to retrieve email");
        }
        return false;
    }

    public boolean usernameExists(String username) throws DataAccessException {
        var statement = "SELECT username FROM user_data WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to retrieve username");
        }
        return false;
    }

    private String encodePassword(String password) {
        String hashedPassword = passwordEncoder.encode(password);
        return hashedPassword;
    }

    public UserData getUser(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM user_data WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        UserData user = readUser(rs);
                        if (passwordEncoder.matches(password, user.password())){
                            return user;
                        } else {
                            throw new DataAccessException("Error: Incorrect password");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to get user");
        }
        return null;
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user_data";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        var user = new UserData(username, password, email);
        return user;
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Unable to update database");
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user_data (
              username VARCHAR(256) NOT NULL PRIMARY KEY,
              password VARCHAR(256) NOT NULL,
              email VARCHAR(256) NOT NULL
            );
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to configure database");
        }
    }
}
