package DAOTests;

import chess.ChessGame;
import dataAccess.MemoryGameDAO;
import dataAccess.GameDAO;
import model.GameData;
import model.ListGameRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.DataAccessException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {

    private GameDAO gameDAO;
    private GameData game;
    private UserData user;

    @BeforeEach
    public void setUp() {
        gameDAO = new MemoryGameDAO();
        game = new GameData(1, "whiteUsername", "blackUsername", "gameName", new ChessGame());
        user = new UserData("akirkman", "Ashsmash47!", "kirkmash13@gmail.com");
    }

    @Test
    public void testGameAlreadyCreated() throws DataAccessException {
        gameDAO.createGame(user.username());
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(user.username()));
    }

    @Test
    public void testCreateAndGetGame() throws DataAccessException {
        gameDAO.createGame(game.gameName());
        ChessGame chessGame = new ChessGame();

        GameData retrievedGame = gameDAO.getGame(game.gameID());
        assertEquals(1, retrievedGame.gameID());
        assertEquals("gameName", retrievedGame.gameName());
        assertNull(retrievedGame.whiteUsername());
        assertNull(retrievedGame.blackUsername());
        assertEquals(chessGame, retrievedGame.game());
    }

    @Test
    public void testClear() throws DataAccessException {
        gameDAO.createGame(game.gameName());
        gameDAO.clear();
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(game.gameID()));
    }

    @Test
    public void testUpdateGame() throws DataAccessException {
        gameDAO.createGame(game.gameName());
        GameData updatedGame = new GameData(1, "newWhiteUsername", "newBlackUsername", "newGameName", new ChessGame());
        gameDAO.updateGame(updatedGame);
        GameData retrievedGame = gameDAO.getGame(updatedGame.gameID());
        assertEquals(1, retrievedGame.gameID());
        assertEquals("newGameName", retrievedGame.gameName());
        assertEquals("newWhiteUsername", retrievedGame.whiteUsername());
        assertEquals("newBlackUsername", retrievedGame.blackUsername());
    }

    @Test
    public void testGetGameNotFound() throws DataAccessException{
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(1));
    }

    @Test
    public void testListGames() throws DataAccessException {
        gameDAO.createGame(game.gameName());
        gameDAO.updateGame(new GameData(game.gameID(), user.username(), null, game.gameName(), game.game()));
        String gameName = "gameName2";
        gameDAO.createGame(gameName);
        GameData game2 = gameDAO.getGame(2);
        gameDAO.updateGame(new GameData(game2.gameID(), user.username(), null, gameName, game2.game()));
        List<ListGameRequest> gameList = gameDAO.listGames(user.username());
        for (ListGameRequest game : gameList) {
            System.out.println(game.gameName());
        }
    }
}
