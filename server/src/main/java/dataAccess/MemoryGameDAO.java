package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class MemoryGameDAO implements GameDAO{
    private Map<Integer, GameData> games;

    public MemoryGameDAO() {
        this.games = new HashMap<>();
    }

    public int createGame(String gameName) throws DataAccessException {
        for (GameData g : games.values()) {
            if (g.gameName().equals(gameName)) {
                throw new DataAccessException("Error: Game name already exists");
            }
        }
        int gameID = games.size() + 1;
        GameData game = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, game);
        return gameID;
    }
    public GameData getGame(int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game with ID " + gameID + " not found");
        }
        return games.get(gameID);
    }

    public List<GameData> listGames(String username) throws DataAccessException {
        List<GameData> userGames = new ArrayList<>();
        for (GameData g : games.values()) {
            if ((g.whiteUsername() != null && g.whiteUsername().equals(username)) || (g.blackUsername() != null && g.blackUsername().equals(username))) {
                userGames.add(g);
            }
        }
        return userGames;
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
