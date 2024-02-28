package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import model.JoinGameRequest;
import service.JoinService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;


public class JoinGameHandler implements Route {

    private Gson gson = new Gson();
    private JoinService joinService;

    public JoinGameHandler(JoinService joinService) {
        this.joinService = joinService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (request.requestMethod().equalsIgnoreCase("PUT")) {
                String authToken = request.headers("Authorization");
                if (authToken != null) {
                    JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
                    joinService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
                    response.status(HttpURLConnection.HTTP_OK);
                    return "{}";
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
