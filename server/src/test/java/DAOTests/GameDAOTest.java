package DAOTests;

import chess.ChessGame;
import dataAccess.MemoryGameDAO;
import dataAccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dataAccess.DataAccessException;
import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTest {

    private GameDAO gameDAO;
    private GameData game;

    @BeforeEach
    public void setUp() {
        gameDAO = new MemoryGameDAO();
        game = new GameData(1, "whiteUsername", "blackUsername", "gameName", new ChessGame());
    }

    @Test
    public void testGameAlreadyCreated() throws DataAccessException {
        gameDAO.createGame(game);
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(game));
    }

    @Test
    public void testCreateAndGetGame() throws DataAccessException {
        gameDAO.createGame(game);
        ChessGame chessGame = new ChessGame();

        GameData retrievedGame = gameDAO.getGame(game.gameID());
        assertEquals(1, retrievedGame.gameID());
        assertEquals("gameName", retrievedGame.gameName());
        assertEquals("whiteUsername", retrievedGame.whiteUsername());
        assertEquals("blackUsername", retrievedGame.blackUsername());
        assertEquals("gameName", retrievedGame.gameName());
        assertEquals(chessGame, retrievedGame.game());
    }

    @Test
    public void testClear() throws DataAccessException {
        gameDAO.createGame(game);
        gameDAO.clear();
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(game.gameID()));
    }

    @Test
    public void testUpdateGame() throws DataAccessException {
        gameDAO.createGame(game);
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
        gameDAO.createGame(game);
        GameData game2 = new GameData(2, "whiteUsername2", "blackUsername2", "gameName2", new ChessGame());
        gameDAO.createGame(game2);
        for(GameData game : gameDAO.listGames()) {
            System.out.println(game.game());
        }
        assertEquals(2, gameDAO.listGames().size());
    }
}
