package DAOTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTests {

    private UserDAO db;

    private UserDAO getUserDAO() throws DataAccessException {
        UserDAO db;
        db = new MySqlUserDAO();
        db.clear();
        return db;
    }

    @BeforeEach
    public void setUp() throws DataAccessException, SQLException {
        db = getUserDAO();
        DatabaseManager.getConnection().setAutoCommit(false);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.clear();
    }

    @Test
    public void testCreateUser() throws DataAccessException {
        UserData user = new UserData("akirkman", "Ashsmash47!", "kirkmash13@gmail.com");
        assertDoesNotThrow(() -> db.createUser(user));
    }

    @Test
    public void testCreateUserTwice() throws DataAccessException {
        UserData user = new UserData("user", "pass", "email");
        db.createUser(user);
        assertThrows(DataAccessException.class, () -> db.createUser(user));
    }

    @Test
    public void testIncorrectPassword() throws DataAccessException {
        UserData user = new UserData("user", "pass", "email");
        db.createUser(user);
        assertThrows(DataAccessException.class, () -> db.getUser(user.username(), "wrong"));
    }

    @Test
    public void testEmailAlreadyInUse() throws DataAccessException {
        UserData user = new UserData("user", "pass", "email");
        db.createUser(user);
        UserData user2 = new UserData("user2", "pass2", "email");
        assertThrows(DataAccessException.class, () -> db.createUser(user2));
    }

    @Test
    public void testGetNonexistentUser() throws DataAccessException {
        UserData user = new UserData("user", "pass", "email");
        assertNull(db.getUser(user.username(), user.password()));
    }
}
