package ServiceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.AuthDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.DataAccessException;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;


public class RegistrationServiceTests {

    private RegistrationService registrationService = new RegistrationService();
    private UserData user;

    @BeforeEach
    public void setUp() {
        registrationService = new RegistrationService();
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
