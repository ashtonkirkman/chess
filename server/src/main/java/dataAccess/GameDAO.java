package dataAccess;

import model.GameData;
import model.ListGameRequest;

import java.util.List;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<ListGameRequest> listGames(String username) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    void clear() throws DataAccessException;
}