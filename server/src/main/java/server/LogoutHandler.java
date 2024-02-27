package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class LogoutHandler implements Route {

    private Gson gson = new Gson();
    private LoginService loginService;

    public LogoutHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (request.requestMethod().equalsIgnoreCase("DELETE")) {
                String authToken = request.headers("Authorization");
                if (authToken != null) {
                    loginService.logout(authToken);
                    response.status(HttpURLConnection.HTTP_OK);
                    return successResponse("{}");
                }
            }
            response.status(HttpURLConnection.HTTP_BAD_REQUEST);
            return errorResponse("Invalid request or missing Authorization header");
        } catch (Exception e) {
            response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return errorResponse(e.getMessage());
        }
    }

    private Object successResponse(String message) {
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", message);
        return gson.toJson(jsonResponse);
    }

    private Object errorResponse(String message) {
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("error", message);
        return gson.toJson(jsonResponse);
    }
}
