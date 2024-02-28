package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import exception.ResponseException;
import exception.UnauthorizedException;
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
import java.util.List;


public class ListGamesHandler implements Route {

    private Gson gson = new Gson();
    private JoinService joinService;

    public ListGamesHandler(JoinService joinService) {
        this.joinService = joinService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String authToken = request.headers("Authorization");
            List<ListGameRequest> games = joinService.listGames(authToken);
            ListGameResponse listGameResponse = new ListGameResponse(games);
            String gamesJson = gson.toJson(listGameResponse);
            return gamesJson;
        } catch (UnauthorizedException e) {
            throw new ResponseException(e.statusCode(), e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseException(401, e.getMessage());
        }
    }
}
