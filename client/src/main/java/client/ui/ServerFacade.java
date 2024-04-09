package client.ui;
import exception.ResponseException;
import model.*;
import com.google.gson.Gson;

import java.io.IOException;

public class ServerFacade {
    private ClientCommunicator communicator;
    private final String serverUrl;

    public ServerFacade(String url) {
        this.communicator = new ClientCommunicator();
        this.serverUrl = url;
    }

    public void clear(String authToken) throws IOException, ResponseException {
        String urlString = serverUrl + "/db";
        communicator.doDelete(urlString, authToken);
    }

    public String register(String username, String password, String email) throws ResponseException, IOException {
        String urlString = serverUrl + "/user";
        UserData user = new UserData(username, password, email);
        String requestBody = new Gson().toJson(user);
        String responseBody = communicator.doPost(urlString, requestBody, null);
        AuthData authData = new Gson().fromJson(responseBody, AuthData.class);
        return authData.authToken();
    }


    public String login(String username, String password) throws IOException, ResponseException {
        LoginRequest request = new LoginRequest(username, password);
        String urlString = serverUrl + "/session";
        String requestBody = new Gson().toJson(request);
        String responseBody = communicator.doPost(urlString, requestBody, null);
        AuthData authData = new Gson().fromJson(responseBody, AuthData.class);
        return authData.authToken();
    }

    public void logout(String authToken) throws IOException, ResponseException {
        String urlString = serverUrl + "/session";
        communicator.doDelete(urlString, authToken);
    }

    public void createGame(String authToken, String gameName) throws IOException, ResponseException {
        String urlString = serverUrl + "/game";
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        String requestBody = new Gson().toJson(createGameRequest);
        communicator.doPost(urlString, requestBody, authToken);
    }

    public void joinGame(String authToken, int gameId, String playerColor) throws IOException, ResponseException {
        String urlString = serverUrl + "/game";
        JoinGameRequest joinGameRequest = new JoinGameRequest(gameId, playerColor);
        String requestBody = new Gson().toJson(joinGameRequest);
        communicator.doPut(urlString, requestBody, authToken);
    }

    public void observeGame(String authToken, int gameId) throws IOException, ResponseException {
        String urlString = serverUrl + "/game";
        JoinGameRequest joinGameRequest = new JoinGameRequest(gameId, null);
        String requestBody = new Gson().toJson(joinGameRequest);
        communicator.doPut(urlString, requestBody, authToken);
    }

    public ListGameResponse listGames(String authToken) throws IOException, ResponseException {
        String urlString = serverUrl + "/game";
        String responseBody = communicator.doGet(urlString, authToken);
        ListGameResponse listGameResponse = new Gson().fromJson(responseBody, ListGameResponse.class);
        return listGameResponse;
    }
}
