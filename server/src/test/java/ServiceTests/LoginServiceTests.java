package ServiceTests;

import dataAccess.*;
import exception.EmptyCredentialsException;
import exception.UnauthorizedException;
import exception.UsernameExistsException;
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
        assertThrows(DataAccessException.class, () -> loginService.login(user.username(), user.password()));
    }

    @Test
    public void testLogin() throws DataAccessException, EmptyCredentialsException, UsernameExistsException, UnauthorizedException {
        String authToken = registrationService.register(user);
        loginService.logout(authToken);
        authToken = loginService.login(user.username(), user.password());
        System.out.println(authToken);
        assertNotNull(authToken);
    }

    @Test
    public void testAlreadyLoggedIn() throws DataAccessException, EmptyCredentialsException, UsernameExistsException {
        registrationService.register(user);
        loginService.login(user.username(), user.password());
    }

    @Test
    public void testLogoutTwice() throws DataAccessException, EmptyCredentialsException, UsernameExistsException, UnauthorizedException {
        String authToken = registrationService.register(user);
        loginService.logout(authToken);
        assertThrows(UnauthorizedException.class, () -> loginService.logout(authToken));
    }
}
