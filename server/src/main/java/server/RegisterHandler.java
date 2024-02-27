package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.RegistrationService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;


public class RegisterHandler implements Route {

    private Gson gson = new Gson();
    private RegistrationService registrationService;

    public RegisterHandler(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            if (request.requestMethod().equalsIgnoreCase("POST")) {
                String reqData = request.body();
                UserData user = gson.fromJson(reqData, UserData.class);
                String authToken = registrationService.register(user);
                AuthData authData = new AuthData(authToken, user.username());
                String authTokenJson = gson.toJson(authData);

                response.status(HttpURLConnection.HTTP_OK);
                return authTokenJson;
            } else {
                response.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return errorResponse("Invalid request method");
            }
        } catch (DataAccessException e) {
            response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
//            e.printStackTrace();
            return errorResponse(e.getMessage());
        }
    }

    private String errorResponse(String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return gson.toJson(errorResponse);
    }
}
