package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.ListGameRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public int createGame(String gameName) throws DataAccessException {
        if (gameNameExists(gameName)) {
            throw new DataAccessException("Error: Game name already exists");
        }
        var statement = "INSERT INTO game_data (game_name, json) VALUES(?,?)";
        ChessGame game = new ChessGame();
        String json = new Gson().toJson(game);
        executeUpdate(statement, gameName, json);
        statement = "SELECT game_id FROM game_data WHERE game_name = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int gameId = rs.getInt("game_id");
                        return gameId;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to retrieve game id");
        }
        return 0;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT * FROM game_data WHERE game_id = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to retrieve game");
        }
        throw new DataAccessException("Error: Game with id: " + gameID + " not found");
    }

    public List<ListGameRequest> listGames(String username) throws DataAccessException {
        var statement = "SELECT * FROM game_data";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    var games = new ArrayList<ListGameRequest>();
                    while (rs.next()) {
                        var gameID = rs.getInt("game_id");
                        var gameName = rs.getString("game_name");
                        var whiteUsername = rs.getString("white_username");
                        var blackUsername = rs.getString("black_username");
                        games.add(new ListGameRequest(gameID, gameName, whiteUsername, blackUsername));
                    }
                    return games;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to retrieve games");
        }
    }

    public void updateGame(GameData game) throws DataAccessException {
        var gameId = game.gameID();
        if (!gameIdExists(gameId)) {
            throw new DataAccessException("Error: Game with ID \"" + gameId + "\" not found");
        }

        var statement = "UPDATE game_data SET white_username = ?, black_username = ?, json = ?, game_name = ? WHERE game_id = ?";
        var json = new Gson().toJson(game.game());
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), json, game.gameName(), game.gameID());
    }

    public boolean gameIdExists(int gameId) throws DataAccessException {
        var statement = "SELECT game_id FROM game_data WHERE game_id = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to retrieve gameId");
        }
        return false;
    }

    public boolean gameNameExists(String gameName) throws DataAccessException {
        var statement = "SELECT game_name FROM game_data WHERE game_name = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to retrieve gameName");
        }
        return false;
    }

    public void clear() throws DataAccessException {
        var statement = "TRUNCATE game_data";
        executeUpdate(statement);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameId = rs.getInt("game_id");
        var whiteUsername = rs.getString("white_username");
        var blackUsername = rs.getString("black_username");
        var gameName = rs.getString("game_name");
        var json = rs.getString("json");
        var game = new Gson().fromJson(json, ChessGame.class);
        var gameData = new GameData(gameId, whiteUsername, blackUsername, gameName, game);
        return gameData;
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
            CREATE TABLE IF NOT EXISTS game_data (
              game_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
              white_username VARCHAR(256) DEFAULT NULL,
              black_username VARCHAR(256) DEFAULT NULL,
              game_name VARCHAR(256) NOT NULL,
              json TEXT NOT NULL
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
