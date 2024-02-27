package ServiceTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoginService;
import service.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTests {

    private LoginService loginService;
    private RegistrationService registrationService;
    private UserData user;


    @BeforeEach
    public void setup() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        loginService = new LoginService(userDAO, authDAO);
        registrationService = new RegistrationService(userDAO, authDAO);
        user = new UserData("akirkman", "Ashsmash47!", "kirkmash13@gmail.com");
    }

    @Test
    public void testUsernameDoesNotExist() {
        assertThrows(DataAccessException.class, () -> loginService.login(user));
    }

    @Test
    public void testLogin() throws DataAccessException {
        String authToken = registrationService.register(user);
        loginService.logout(authToken);
        authToken = loginService.login(user);
        System.out.println(authToken);
        assertNotNull(authToken);
    }

    @Test
    public void testAlreadyLoggedIn() throws DataAccessException {
        registrationService.register(user);
        assertThrows(DataAccessException.class, () -> loginService.login(user));
    }
}
