package ServiceTests;

import dataAccess.*;
import exception.EmptyCredentialsException;
import exception.UsernameExistsException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.RegistrationService;
import service.JoinService;
import chess.ChessGame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JoinServiceTests {

    private JoinService joinService;
    private RegistrationService registrationService;
    private String authToken;

    @BeforeEach
    public void setup() throws DataAccessException, EmptyCredentialsException, UsernameExistsException{

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        joinService = new JoinService(userDAO, authDAO, gameDAO);
        registrationService = new RegistrationService(userDAO, authDAO);
        UserData user = new UserData("akirkman", "Ashsmash47!", "kirkmash13@gmail.com");

        authToken = registrationService.register(user);
    }

    @Test
    public void testCreateGame() throws DataAccessException {
        joinService.createGame(authToken, "TestGame");
    }

    @Test
    public void testJoinGame() throws DataAccessException {
        int gameID = joinService.createGame(authToken, "TestGame");
        String teamColor = "WHITE";
        joinService.joinGame(authToken, gameID, teamColor);
    }

   @Test
   public void testListGames() throws DataAccessException {
       joinService.createGame(authToken, "game1");
       joinService.createGame(authToken, "game2");
       joinService.createGame(authToken, "game3");
       joinService.joinGame(authToken, 1, "WHITE");
       joinService.joinGame(authToken, 2, "BLACK");
       joinService.joinGame(authToken, 3, "WHITE");
       List<GameData> games = joinService.listGames(authToken);
       for(GameData game : games) {
           System.out.println(game.gameName());
       }
   }

   @Test
   public void testJoinGameAlreadyHasWhitePlayer() throws DataAccessException, EmptyCredentialsException, UsernameExistsException {
        String authToken2 = registrationService.register(new UserData("user2", "password", "email2"));
        String authToken3 = registrationService.register(new UserData("user3", "password", "email3"));
        int gameID = joinService.createGame(authToken, "game1");
        joinService.joinGame(authToken, gameID, "WHITE");
        joinService.joinGame(authToken2, gameID, "BLACK");
        assertThrows(DataAccessException.class, () -> joinService.joinGame(authToken3, gameID, "WHITE"));
   }
}
