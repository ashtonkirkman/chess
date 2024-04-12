package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataAccess.*;
import exception.BadRequestException;
import exception.ResponseException;
import exception.UnauthorizedException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.JoinService;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final JoinService joinService = new JoinService(new MySqlUserDAO(), new MySqlAuthDAO(), new MySqlGameDAO());
    private final Map<Integer, Boolean> resignedStateMap = new HashMap<>();

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        UserGameCommand command = new Gson().fromJson(msg, UserGameCommand.class);

        switch (command.getCommandType()) {
            case JOIN_PLAYER -> join(session, msg);
            case JOIN_OBSERVER -> observe(session, msg);
            case MAKE_MOVE -> move(msg);
            case LEAVE -> leave(session, msg);
            case RESIGN -> resign(session, msg);
        }
    }

    public void join(Session session, String msg) throws ResponseException {
        JoinPlayerCommand command = new Gson().fromJson(msg, JoinPlayerCommand.class);
        int gameID = command.getGameID();
        String authToken = command.getAuthString();
        connections.addConnection(authToken, gameID, session);

        try {
            joinService.getGame(gameID);
        } catch (BadRequestException e) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: Game does not exist"));
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }

        GameData gameData = joinService.getGame(gameID);
        ChessGame.TeamColor color = command.getPlayerColor();
        ChessGame game = gameData.game();
        try {
            joinService.getUsername(authToken);
        } catch (UnauthorizedException e) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: Unauthorized"));
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }
        String username = joinService.getUsername(authToken);

        if(color == ChessGame.TeamColor.WHITE) {
            if (!Objects.equals(gameData.whiteUsername(), username)) {
                try {
                    connections.sendErrorMessage(authToken, new ErrorMessage("Error: White player already exists"));
                } catch (IOException e) {
                    throw new ResponseException(500, e.getMessage());
                }
                return;
            }
        }
        else if (color == ChessGame.TeamColor.BLACK) {
            if (!Objects.equals(gameData.blackUsername(), username)) {
                try {
                    connections.sendErrorMessage(authToken, new ErrorMessage("Error: Black player already exists"));
                } catch (IOException e) {
                    throw new ResponseException(500, e.getMessage());
                }
                return;
            }
        }

        String responseMessage = String.format("%s has joined the game as %s", username, color.toString());
        NotificationMessage notificationMessage = new NotificationMessage(responseMessage);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        try {
            connections.sendLoadGameMessage(gameID, authToken, loadGameMessage);
            connections.sendNotificationMessage(gameID, authToken, notificationMessage);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void observe(Session session, String msg) throws ResponseException {
        JoinObserverCommand command = new Gson().fromJson(msg, JoinObserverCommand.class);
        int gameID = command.getGameID();
        String authToken = command.getAuthString();
        connections.addConnection(authToken, gameID, session);
        try {
            joinService.getGame(gameID);
        } catch (BadRequestException e) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: Game does not exist"));
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }
        GameData gameData = joinService.getGame(gameID);
        ChessGame game = gameData.game();
        try {
            joinService.getUsername(authToken);
        } catch (UnauthorizedException e) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: Unauthorized"));
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }
        String username = joinService.getUsername(authToken);

        String responseMessage = String.format("%s has joined the game as an observer", username);
        NotificationMessage notificationMessage = new NotificationMessage(responseMessage);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        try {
            connections.sendNotificationMessage(gameID, authToken, notificationMessage);
            connections.sendLoadGameMessage(gameID, authToken, loadGameMessage);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void move(String msg) throws ResponseException {
        MakeMoveCommand command = new Gson().fromJson(msg, MakeMoveCommand.class);
        int gameID = command.getGameID();
        String authToken = command.getAuthString();
        GameData gameData = joinService.getGame(gameID);
        ChessGame game = gameData.game();
        ChessMove move = command.getMove();
        ChessGame.TeamColor teamTurn = game.getTeamTurn();
        String username = joinService.getUsername(authToken);
        String blackUsername = gameData.blackUsername();
        String whiteUsername = gameData.whiteUsername();

        if (resignedStateMap.getOrDefault(gameID, false)) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: Game is over"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        if (teamTurn == ChessGame.TeamColor.WHITE && !Objects.equals(gameData.whiteUsername(), username)) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: It's not your turn"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }
        else if (teamTurn == ChessGame.TeamColor.BLACK && !Objects.equals(gameData.blackUsername(), username)) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: It's not your turn"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        try {
            game.makeMove(move);
            joinService.updateGame(new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
        } catch (Exception e) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: " + e.getMessage()));
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }

        String responseMessage = String.format("%s moved from %s to %s", username, move.getStartPosition().toString(), move.getEndPosition().toString());
        NotificationMessage notificationMessage = new NotificationMessage(responseMessage);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        try {
            connections.sendLoadGameMessage(gameID, "all", loadGameMessage);
            connections.sendNotificationMessage(gameID, authToken, notificationMessage);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }

        if(game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            responseMessage = String.format("%s is in checkmate! %s wins!", whiteUsername, blackUsername);
            notificationMessage = new NotificationMessage(responseMessage);
            try {
                connections.sendNotificationMessage(gameID, "all", notificationMessage);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            responseMessage = String.format("%s is in checkmate! %s wins!", blackUsername, whiteUsername);
            notificationMessage = new NotificationMessage(responseMessage);
            try {
                connections.sendNotificationMessage(gameID, "all", notificationMessage);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        if(game.isInCheck(ChessGame.TeamColor.WHITE)) {
            responseMessage = String.format("%s is in check!", whiteUsername);
            notificationMessage = new NotificationMessage(responseMessage);
            try {
                connections.sendNotificationMessage(gameID, "all", notificationMessage);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            responseMessage = String.format("%s is in check!", blackUsername);
            notificationMessage = new NotificationMessage(responseMessage);
            try {
                connections.sendNotificationMessage(gameID, "all", notificationMessage);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
    }

    public void leave(Session session, String msg) throws ResponseException {
        LeaveCommand leaveCommand = new Gson().fromJson(msg, LeaveCommand.class);
        int gameID = leaveCommand.getGameID();
        GameData gameData = joinService.getGame(gameID);
        String authToken = leaveCommand.getAuthString();
        String username = joinService.getUsername(authToken);
        if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(username)) {
            gameData = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            try {
                updateGame(gameData);
            } catch (DataAccessException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
        else if (gameData.blackUsername() != null && gameData.blackUsername().equals(username)) {
            gameData = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            try {
                updateGame(gameData);
            } catch (DataAccessException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
        String message = String.format("%s has left the game", username);
        NotificationMessage notificationMessage = new NotificationMessage(message);
        try {
            connections.sendNotificationMessage(gameID, authToken, notificationMessage);
            connections.removeConnectionToGame(authToken, gameID);
            connections.removeConnection(session);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(Session session, String msg) throws ResponseException {
        ResignCommand resignCommand = new Gson().fromJson(msg, ResignCommand.class);
        int gameID = resignCommand.getGameID();
        String authToken = resignCommand.getAuthString();
        GameData gameData = joinService.getGame(gameID);
        String username = joinService.getUsername(authToken);
        if (resignedStateMap.getOrDefault(gameID, false)) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: Game is over"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }

        if (!Objects.equals(username, gameData.whiteUsername()) && !Objects.equals(username, gameData.blackUsername())) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: You are an observer"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }
        String message = String.format("%s has resigned", username);
        resignedStateMap.put(gameID, true);
        NotificationMessage notificationMessage = new NotificationMessage(message);
        try {
            connections.sendNotificationMessage(gameID, "none", notificationMessage);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void updateGame(GameData gameData) throws DataAccessException {
        new MySqlGameDAO().updateGame(gameData);
    }
}
