package ServiceTests;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.LoginService;
import service.RegistrationService;
import service.JoinService;
import chess.ChessGame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JoinServiceTests {

    private JoinService joinService;
    private RegistrationService registrationService;
    private UserData user;
    private String authToken;

    @BeforeEach
    public void setup() throws DataAccessException {

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        joinService = new JoinService(userDAO, authDAO, gameDAO);
        registrationService = new RegistrationService(userDAO, authDAO);
        user = new UserData("akirkman", "Ashsmash47!", "kirkmash13@gmail.com");

        authToken = registrationService.register(user);
    }

    @Test
    public void testCreateGame() throws DataAccessException {
        joinService.createGame(authToken, "TestGame");
    }

    @Test
    public void testJoinGame() throws DataAccessException {
        int gameID = joinService.createGame(authToken, "TestGame");
        joinService.joinGame(authToken, gameID, ChessGame.TeamColor.WHITE);
    }

   @Test
   public void testListGames() throws DataAccessException {
       joinService.createGame(authToken, "game1");
       joinService.createGame(authToken, "game2");
       joinService.createGame(authToken, "game3");
       joinService.joinGame(authToken, 1, ChessGame.TeamColor.WHITE);
       joinService.joinGame(authToken, 2, ChessGame.TeamColor.BLACK);
       joinService.joinGame(authToken, 3, ChessGame.TeamColor.WHITE);
       List<GameData> games = joinService.listGames(authToken);
       for(GameData game : games) {
           System.out.println(game.gameName());
       }
   }

   @Test
   public void testJoinGameAlreadyHasWhitePlayer() throws DataAccessException {
        String authToken2 = registrationService.register(new UserData("user2", "password", "email2"));
        String authToken3 = registrationService.register(new UserData("user3", "password", "email3"));
        int gameID = joinService.createGame(authToken, "game1");
        joinService.joinGame(authToken, gameID, ChessGame.TeamColor.WHITE);
        joinService.joinGame(authToken2, gameID, ChessGame.TeamColor.BLACK);
        assertThrows(DataAccessException.class, () -> joinService.joinGame(authToken3, gameID, ChessGame.TeamColor.WHITE));
   }
}
