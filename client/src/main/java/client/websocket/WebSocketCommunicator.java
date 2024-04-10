package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketCommunicator extends Endpoint {

    Session session;
    ServerMessageObserver serverMessageObserver;

    public WebSocketCommunicator(String url, ServerMessageObserver serverMessageObserver) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.serverMessageObserver = serverMessageObserver;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
//                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    serverMessageObserver.notify(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void join(int gameID, String authToken, ChessGame.TeamColor playerColor) throws ResponseException {
        JoinPlayerCommand joinPlayerCommand = new JoinPlayerCommand(authToken, gameID, playerColor);
        String message = new Gson().toJson(joinPlayerCommand, JoinPlayerCommand.class);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void observe(int gameID, String authToken) throws ResponseException {
        JoinObserverCommand joinObserverCommand = new JoinObserverCommand(authToken, gameID);
        String message = new Gson().toJson(joinObserverCommand, JoinObserverCommand.class);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void move(int gameID, String authToken, ChessMove move) throws ResponseException {
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameID, move);
        String message = new Gson().toJson(makeMoveCommand, MakeMoveCommand.class);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(int gameID, String authToken) throws ResponseException {
        LeaveCommand leaveCommand = new LeaveCommand(authToken, gameID);
        String message = new Gson().toJson(leaveCommand, LeaveCommand.class);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(int gameID, String authToken) throws ResponseException {
        ResignCommand resignCommand = new ResignCommand(authToken, gameID);
        String message = new Gson().toJson(resignCommand, ResignCommand.class);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}
