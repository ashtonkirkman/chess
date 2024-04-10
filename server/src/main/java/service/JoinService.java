package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exception.BadRequestException;
import exception.UnauthorizedException;
import exception.UsernameExistsException;
import model.AuthData;
import model.GameData;
import model.ListGameRequest;
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

    public List<ListGameRequest> listGames(String authToken) throws DataAccessException, UnauthorizedException {
        try {
            AuthData authData = authDAO.getAuth(authToken);
            String username = authData.username();
            return gameDAO.listGames(username);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

    public int createGame(String authToken, String gameName) throws DataAccessException, UnauthorizedException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
        try {
            return gameDAO.createGame(gameName);
        } catch (DataAccessException e) {
            throw new UnauthorizedException(e.getMessage());
        }
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws DataAccessException, BadRequestException, UnauthorizedException, UsernameExistsException {
        GameData game;
        String username;

        try {
            authDAO.getAuth(authToken);
            username = authDAO.getAuth(authToken).username();
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: Unauthorized");
        }

        try {
            game = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException("Error: Invalid game ID");
        }

        if (playerColor != null && !playerColor.equalsIgnoreCase("white") && !playerColor.equalsIgnoreCase("black")) {
            throw new BadRequestException("Error: Invalid player color");
        }

        if (playerColor != null && playerColor.equalsIgnoreCase("white")) {
            if (game.whiteUsername() != null) {
                throw new UsernameExistsException("Error: Game already has a white player");
            }
            gameDAO.updateGame(new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
        } else if (playerColor != null && playerColor.equalsIgnoreCase("black")) {
            if (game.blackUsername() != null) {
                throw new UsernameExistsException("Error: Game already has a black player");
            }
            gameDAO.updateGame(new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
        }
    }

    public GameData getGame(int gameID) throws BadRequestException {
        try {
            return gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException("Error: Invalid game ID");
        }
    }

    public String getUsername(String authToken) throws UnauthorizedException {
        try {
            return authDAO.getUsername(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

    public void updateGame(GameData gameData) throws DataAccessException {
        gameDAO.updateGame(gameData);
    }
}
