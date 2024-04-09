package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<Integer, ArrayList<Connection>> connections = new ConcurrentHashMap<>();

    public void addConnection(String authToken, int gameID, Session session) {
        Connection connection = new Connection(authToken, session);
        if (connections.containsKey(gameID)) {
            connections.get(gameID).add(connection);
        }
        else {
            ArrayList<Connection> newConnections = new ArrayList<>();
            newConnections.add(connection);
            connections.put(gameID, newConnections);
        }
    }

    public void removeConnectionToGame(String authToken, int gameID) {
        if (connections.containsKey(gameID)) {
            ArrayList<Connection> connectionsToGame = connections.get(gameID);
            for (Connection connection : connectionsToGame) {
                if (connection.authToken.equals(authToken)) {
                    connectionsToGame.remove(connection);
                    break;
                }
            }
        }
    }

    public void removeConnection(Session session) {
        for (int gameID : connections.keySet()) {
            ArrayList<Connection> connectionsToGame = connections.get(gameID);
            for (Connection connection : connectionsToGame) {
                if (connection.session.equals(session)) {
                    connectionsToGame.remove(connection);
                    break;
                }
            }
        }
    }

    public void sendNotificationMessage(int gameID, String excludedUserAuth, NotificationMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.get(gameID)) {
            if (c.session.isOpen()) {
                if (!c.authToken.equals(excludedUserAuth)) {
                    String messageString = new Gson().toJson(message, NotificationMessage.class);
                    c.send(messageString);
                }
            }
            else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c);
        }
    }

    public void sendErrorMessage(String authToken, ErrorMessage errorMessage) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (int gameID : connections.keySet()) {
            for (var c : connections.get(gameID)) {
                if (c.session.isOpen()) {
                    if (c.authToken.equals(authToken)) {
                        String messageString = new Gson().toJson(errorMessage, ErrorMessage.class);
                        c.send(messageString);
                    }
                }
                else {
                    removeList.add(c);
                }
            }

            // Clean up any connections that were left open.
            for (var c : removeList) {
                connections.get(gameID).remove(c);
            }
        }
    }

    public void sendLoadGameMessage(int gameID, String authToken, LoadGameMessage loadGameMessage) throws IOException{
        var removeList = new ArrayList<Connection>();
        if(authToken.equals("all")) {
            for (var c : connections.get(gameID)) {
                if (c.session.isOpen()) {
                    String messageString = new Gson().toJson(loadGameMessage, LoadGameMessage.class);
                    c.send(messageString);
                }
                else {
                    removeList.add(c);
                }
            }
        }
        else {
            for (var c : connections.get(gameID)) {
                if (c.session.isOpen()) {
                    if (c.authToken.equals(authToken)) {
                        String messageString = new Gson().toJson(loadGameMessage, LoadGameMessage.class);
                        c.send(messageString);
                    }
                }
                else {
                    removeList.add(c);
                }
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.get(gameID).remove(c);
        }
    }
}
