package dataAccessTests;

import chess.ChessGame;
import dataAccess.*;
import model.GameData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTests {

    private AuthDAO db;

    private AuthDAO getAuthDAO() throws DataAccessException {
        AuthDAO db;
        db = new MySqlAuthDAO();
        db.clear();
        return db;
    }

    @BeforeEach
    public void setUp() throws DataAccessException, SQLException {
        db = getAuthDAO();
        DatabaseManager.getConnection().setAutoCommit(false);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.clear();
    }

    @Test
    public void testCreateAuth() throws DataAccessException {
        assertDoesNotThrow(() -> db.createAuth("username"));
    }

    @Test
    public void testCreateAuthTwice() throws DataAccessException {
        db.createAuth("username");
        assertDoesNotThrow(() -> db.createAuth("username"));
    }

    @Test
    public void testGetAuth() throws DataAccessException {
        String authToken = db.createAuth("username");
        assertNotNull(db.getAuth(authToken));
    }

    @Test
    public void testGetAuthNotFound() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> db.getAuth("nonexistentAuthToken"));
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        String authToken = db.createAuth("username");
        db.deleteAuth(authToken);
        assertThrows(DataAccessException.class, () -> db.getAuth(authToken));
    }

    @Test
    public void testClear() throws DataAccessException {
        String authToken = db.createAuth("username");
        db.clear();
        assertThrows(DataAccessException.class, () -> db.getAuth(authToken));
    }
}