package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataAccess.*;
import exception.ResponseException;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.JoinService;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
    private final JoinService joinService = new JoinService(new MySqlUserDAO(), new MySqlAuthDAO(), new MySqlGameDAO());

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
        GameData gameData = joinService.getGame(gameID);
        if(gameData == null) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: Game does not exist"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }
        ChessGame.TeamColor color = command.getPlayerColor();
        ChessGame game = gameData.game();
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
            connections.sendNotificationMessage(gameID, authToken, notificationMessage);
            connections.sendLoadGameMessage(gameID, authToken, loadGameMessage);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void observe(Session session, String msg) throws ResponseException {
        JoinObserverCommand command = new Gson().fromJson(msg, JoinObserverCommand.class);
        int gameID = command.getGameID();
        String authToken = command.getAuthString();
        connections.addConnection(authToken, gameID, session);
        GameData gameData = joinService.getGame(gameID);
        if(gameData == null) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: Game does not exist"));
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
            return;
        }
        ChessGame game = gameData.game();
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
        String username = joinService.getUsername(authToken);

        try {
            game.makeMove(move);
        } catch (Exception e) {
            try {
                connections.sendErrorMessage(authToken, new ErrorMessage("Error: " + e.getMessage()));
            } catch (IOException ex) {
                throw new ResponseException(500, ex.getMessage());
            }
            return;
        }

        String responseMessage = String.format("%s moved from %s to %s", username, move.getStartPosition(), move.getEndPosition());
        NotificationMessage notificationMessage = new NotificationMessage(responseMessage);
        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        try {
            connections.sendNotificationMessage(gameID, authToken, notificationMessage);
            connections.sendLoadGameMessage(gameID, "all", loadGameMessage);
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }

        if(game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            responseMessage = "Checkmate! Black wins!";
            notificationMessage = new NotificationMessage(responseMessage);
            try {
                connections.sendNotificationMessage(gameID, "all", notificationMessage);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            responseMessage = "Checkmate! White wins!";
            notificationMessage = new NotificationMessage(responseMessage);
            try {
                connections.sendNotificationMessage(gameID, "all", notificationMessage);
            } catch (IOException e) {
                throw new ResponseException(500, e.getMessage());
            }
        }
    }
}
