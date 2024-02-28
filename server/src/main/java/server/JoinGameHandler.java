package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import exception.BadRequestException;
import exception.ResponseException;
import exception.UnauthorizedException;
import exception.UsernameExistsException;
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
            String authToken = request.headers("Authorization");
            JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);
            joinService.joinGame(authToken, joinGameRequest.gameID(), joinGameRequest.playerColor());
            return "{}";
        } catch (UnauthorizedException | BadRequestException | UsernameExistsException e) {
            throw new ResponseException(e.statusCode(), e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
