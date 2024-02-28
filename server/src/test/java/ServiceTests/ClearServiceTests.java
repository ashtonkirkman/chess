package ServiceTests;

import dataAccess.*;
import exception.EmptyCredentialsException;
import exception.UnauthorizedException;
import exception.UsernameExistsException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.RegistrationService;
import service.JoinService;
import chess.ChessGame;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {

    private ClearService clearService;
    private RegistrationService registrationService;
    private JoinService joinService;
    private String authToken;
    private int gameID;

    @BeforeEach
    public void setup() throws DataAccessException, EmptyCredentialsException, UsernameExistsException, UnauthorizedException {

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO, authDAO, gameDAO);
        registrationService = new RegistrationService(userDAO, authDAO);
        joinService = new JoinService(userDAO, authDAO, gameDAO);
        UserData user = new UserData("akirkman", "Ashsmash47!", "kirkmash13@gmail.com");

        authToken = registrationService.register(user);
        gameID = joinService.createGame(authToken, "TestGame");
    }

    @Test
    public void testClear() throws DataAccessException {
        clearService.clear();
        assertThrows(DataAccessException.class, () -> joinService.joinGame(authToken, gameID, "WHITE"));
    }
}
