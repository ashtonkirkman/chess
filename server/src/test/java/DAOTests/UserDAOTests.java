package DAOTests;

import dataAccess.MemoryUserDAO;
import dataAccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.DataAccessException;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTests {

    private UserDAO userDAO;
    private UserData user;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        user = new UserData("akirkman", "Ashsmash47!", "kirkmash13@gmail.com");
    }

    @Test
    public void testUserAlreadyCreated() throws DataAccessException {
        userDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    public void testCreateAndGetUser() throws DataAccessException {
        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser(user.username(), user.password());
        assertEquals("akirkman", retrievedUser.username());
        assertEquals("Ashsmash47!", retrievedUser.password());
        assertEquals("kirkmash13@gmail.com", retrievedUser.email());
    }

    @Test
    public void testClear() throws DataAccessException {
        userDAO.createUser(user);
        userDAO.clear();
        assertNull(userDAO.getUser(user.username(), user.password()));
    }

    @Test
    public void testEmailAlreadyInUse() throws DataAccessException {
        userDAO.createUser(user);
        UserData user2 = new UserData("kirkmash13", "Ashsmash47!", "kirkmash13@gmail.com");
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user2));
    }
}

