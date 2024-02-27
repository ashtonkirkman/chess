package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import service.ClearService;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class ClearHandler implements Route {

    private ClearService clearService;
    private Gson gson = new Gson();

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (request.requestMethod().equalsIgnoreCase("DELETE")) {
                clearService.clear();
                response.status(HttpURLConnection.HTTP_OK);
                return "{}";
            } else {
                response.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return errorResponse("Invalid request");
            }
        } catch (Exception e) {
            response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
            e.printStackTrace();
            return errorResponse("Error clearing data");
        }
    }

    private String successResponse(String message) {
        Map<String, String> jsonResponse = new HashMap<>();
        jsonResponse.put("message", message);
        return gson.toJson(jsonResponse);
    }

    private String errorResponse(String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Error: " + message);
        return gson.toJson(errorResponse);
    }
}
