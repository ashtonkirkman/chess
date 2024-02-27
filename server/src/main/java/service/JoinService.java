package service;

import dataAccess.DataAccessException;

import dataAccess.GameDAO;
import dataAccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame;

import dataAccess.AuthDAO;

import java.util.List;

public class JoinService {

    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public JoinService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        return gameDAO.listGames(username);
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        authDAO.getAuth(authToken);
        return gameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        GameData game = gameDAO.getGame(gameID);
        if (playerColor.toLowerCase().equals("white")) {
            if(game.whiteUsername() != null) {
                throw new DataAccessException("Error: Game already has a white player");
            }
            gameDAO.updateGame(new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
        } else {
            if(game.blackUsername() != null) {
                throw new DataAccessException("Error: Game already has a black player");
            }
            gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
        }
    }
}
