package ServiceTests;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;


public class RegistrationServiceTests {

    private RegistrationService registrationService;
    private UserData user;

    @BeforeEach
    public void setUp() {
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        registrationService = new RegistrationService(userDAO, authDAO);
        user = new UserData("akirkman", "Ashsmash47!", "kirkmash13@gmail.com");
    }

    @Test
    public void testRegister() throws DataAccessException {
        String authToken = registrationService.register(user);
        assertNotNull(authToken);
    }

    @Test
    public void testRegisterUserAlreadyExists() throws DataAccessException {
        registrationService.register(user);
        assertThrows(DataAccessException.class, () -> registrationService.register(user));
    }
}
