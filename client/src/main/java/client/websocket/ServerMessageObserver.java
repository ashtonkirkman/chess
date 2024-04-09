package client.websocket;
import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageObserver {
    void notify(String message);
}
