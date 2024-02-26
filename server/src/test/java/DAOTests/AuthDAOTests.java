package DAOTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.AuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.DataAccessException;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTests {

    private AuthDAO authDAO;
    private String username;

    @BeforeEach
    public void setUp() {
        authDAO = new MemoryAuthDAO();
        username = "username";
    }

    @Test
    public void testAuthAlreadyCreated() throws DataAccessException {
        authDAO.createAuth(username);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(username));
    }

    @Test
    public void testCreateAndGetAuth() throws DataAccessException {
        String authToken = authDAO.createAuth(username);

        AuthData retrievedAuth = authDAO.getAuth(authToken);
        assertEquals(authToken, retrievedAuth.authToken());
        assertEquals("username", retrievedAuth.username());
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        String authToken = authDAO.createAuth(username);
        authDAO.deleteAuth(authToken);
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(authToken));
    }

    @Test
    public void testGetAuthNotFound() throws DataAccessException{
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("authTokenWhichDoesNotExist"));
    }

    @Test
    public void testUsernameAlreadyLoggedIn() throws DataAccessException {
        authDAO.createAuth(username);
        AuthData auth2 = new AuthData("authToken2", "username");
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth2.username()));
    }

    @Test
    public void testClear() throws DataAccessException {
        authDAO.createAuth(username);
        authDAO.clear();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(username));
    }

}
