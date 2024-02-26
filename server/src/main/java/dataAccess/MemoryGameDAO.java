package dataAccess;

import model.GameData;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{
    private Map<Integer, GameData> games;
    private int nextID;

    public MemoryGameDAO() {
        this.games = new HashMap<>();
        this.nextID = 1;
    }

    public void createGame(GameData game) throws DataAccessException {
        try {
            game = new GameData(nextID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
            games.put(nextID, game);
            nextID++;
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to create game");
        }
    }
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game with ID " + gameID + " not found");
        }
        return games.get(gameID);
    }

    public List<GameData> listGames() throws DataAccessException {
        try {
            return new ArrayList<>(games.values());
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to list games");
        }
    }

    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("Game with ID \"" + game.gameID() + "\" not found");
        }
        games.put(game.gameID(), game);
    }

    public void clear() throws DataAccessException {
        try {
            games.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to clear data");
        }
    }
}
