package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.sql.Types.NULL;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() {
        try {
            configureDatabase();
        } catch (DataAccessException e) {
            System.out.println(e.getMessage());
        }
    }

    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth_data (auth_token, username) VALUES(?,?)";
        executeUpdate(statement, authToken, username);
        return authToken;
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM auth_data WHERE auth_token = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var authData = new AuthData(rs.getString("auth_token"), rs.getString("username"));
                        return authData;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to retrieve auth token");
        }
        throw new DataAccessException("Error: Auth token not found");
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (!authExists(authToken)) {
            throw new DataAccessException("Error: Auth token not found");
        }
        var statement = "DELETE FROM auth_data WHERE auth_token = ?";
        executeUpdate(statement, authToken);
    }

    private boolean authExists(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM auth_data WHERE auth_token = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to check if auth token exists");
        }
    }

    public String getUsername(String authToken) throws DataAccessException {
        var statement = "SELECT username FROM auth_data WHERE auth_token = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to retrieve username");
        }
        throw new DataAccessException("Error: Username not found");
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE auth_data";
        executeUpdate(statement);
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
            CREATE TABLE IF NOT EXISTS auth_data (
                auth_token VARCHAR(256) NOT NULL PRIMARY KEY,
                username VARCHAR(256) NOT NULL
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
