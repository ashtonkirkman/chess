package DAOTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.AuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.DataAccessException;
import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {

    private AuthDAO authDAO;
    private AuthData auth;

    @BeforeEach
    public void setUp() {
        authDAO = new MemoryAuthDAO();
        auth = new AuthData("authToken", "username");
    }

    @Test
    public void testAuthAlreadyCreated() throws DataAccessException {
        authDAO.createAuth(auth);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    public void testCreateAndGetAuth() throws DataAccessException {
        authDAO.createAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth(auth.authToken());
        assertEquals("authToken", retrievedAuth.authToken());
        assertEquals("username", retrievedAuth.username());
    }

    @Test
    public void testDeleteAuth() throws DataAccessException {
        authDAO.createAuth(auth);
        authDAO.deleteAuth(auth.authToken());
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth.authToken()));
    }

    @Test
    public void testGetAuthNotFound() throws DataAccessException{
        assertThrows(DataAccessException.class, () -> authDAO.getAuth("authTokenWhichDoesNotExist"));
    }

    @Test
    public void testUsernameAlreadyLoggedIn() throws DataAccessException {
        authDAO.createAuth(auth);
        AuthData auth2 = new AuthData("authToken2", "username");
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth2));
    }

    @Test
    public void testClear() throws DataAccessException {
        authDAO.createAuth(auth);
        authDAO.clear();
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth.authToken()));
    }

}
