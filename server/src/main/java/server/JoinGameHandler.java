package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import model.AuthData;
import dataAccess.*;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import service.JoinService;
import service.LoginService;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;


public class JoinGameHandler implements HttpHandler {

    private Gson gson = new Gson();
    private JoinService joinService;

    public JoinGameHandler(JoinService joinService) {
        this.joinService = joinService;
    }

    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("put")) {

                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");
                    InputStream reqBody = exchange.getRequestBody();
                    String reqData = Utils.readString(reqBody);
                    JoinGameRequest request = gson.fromJson(reqData, JoinGameRequest.class);
                    joinService.joinGame(authToken, request.gameID(), request.playerColor());

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                    success = true;
                }
            }
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                exchange.getResponseBody().close();
            }
        }
        catch (IOException | DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
