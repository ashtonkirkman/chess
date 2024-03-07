package DAOTests;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTests {

    private GameDAO db;

    private GameDAO getGameDAO() throws DataAccessException {
        GameDAO db;
        db = new MySqlGameDAO();
        db.clear();
        return db;
    }

    @BeforeEach
    public void setUp() throws DataAccessException, SQLException {
        db = getGameDAO();
        DatabaseManager.getConnection().setAutoCommit(false);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.clear();
    }

    @Test
    public void testCreateGame() throws DataAccessException {
        assertDoesNotThrow(() -> db.createGame("game"));
    }

    @Test
    public void testCreateGameTwice() throws DataAccessException {
        db.createGame("game");
        assertThrows(DataAccessException.class, () -> db.createGame("game"));
    }

    @Test
    public void testGetGame() throws DataAccessException {
        int gameID = db.createGame("game");
        assertNotNull(db.getGame(gameID));
    }

    @Test
    public void testGetGameNotFound() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> db.getGame(0));
    }

    @Test
    public void testUpdateGame() throws DataAccessException {
        int gameID = db.createGame("game");
        GameData game = new GameData(gameID, "white", "black", "newGameName", new ChessGame());
        db.updateGame(game);
        assertEquals("black", db.getGame(gameID).blackUsername());
        assertEquals("white", db.getGame(gameID).whiteUsername());
        assertEquals("newGameName", db.getGame(gameID).gameName());
    }

    @Test
    public void testUpdateGameNotFound() throws DataAccessException {
        GameData game = new GameData(0, "white", "black", "newGameName", new ChessGame());
        assertThrows(DataAccessException.class, () -> db.updateGame(game));
    }
}
