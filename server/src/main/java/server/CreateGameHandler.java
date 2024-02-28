package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import model.*;
import dataAccess.*;
import service.JoinService;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class CreateGameHandler implements Route {

    private Gson gson = new Gson();
    private JoinService joinService;

    public CreateGameHandler(JoinService joinService) {
        this.joinService = joinService;
    }

    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");
                    InputStream reqBody = exchange.getRequestBody();
                    String reqData = Utils.readString(reqBody);
                    CreateGameRequest request = gson.fromJson(reqData, CreateGameRequest.class);
                    int gameID = joinService.createGame(authToken, request.gameName());
                    CreateGameResponse response = new CreateGameResponse(gameID);
                    String responseJson = gson.toJson(response);

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStream resBody = exchange.getResponseBody();
                    resBody.write(responseJson.getBytes());
                    resBody.close();

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


    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (request.requestMethod().equalsIgnoreCase("POST")) {
                String authToken = request.headers("Authorization");
                if (authToken != null) {
                    CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);
                    int gameID = joinService.createGame(authToken, createGameRequest.gameName());
                    CreateGameResponse createGameResponse = new CreateGameResponse(gameID);
                    response.status(HttpURLConnection.HTTP_OK);
                    return gson.toJson(createGameResponse);
                }
            }
            response.status(HttpURLConnection.HTTP_BAD_REQUEST);
            return errorResponse("Invalid request or missing Authorization header");
        } catch (DataAccessException e) {
            response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return errorResponse(e.getMessage());
        }
    }

    private String errorResponse(String message) {
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", message);
        return gson.toJson(jsonResponse);
    }
}
