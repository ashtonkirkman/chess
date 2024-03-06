package dataAccess;

import com.google.gson.Gson;
import model.UserData;
import java.sql.*;
import static java.sql.Types.NULL;

public class MySqlUserDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user_data (username, password, email, json) VALUES(?,?,?,?)";
        var json = new Gson().toJson(user);
        executeUpdate(statement, user.username(), user.password(), user.email(), json);
    }

    public UserData getUser(String username, String password) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM user_data WHERE username = ?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to get user");
        }
        return null;
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE user_data";
        executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var json = rs.getString("json");
        var user = new Gson().fromJson(json, UserData.class);
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
              email VARCHAR(256) NOT NULL,
              INDEX(username),
              INDEX(password),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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
