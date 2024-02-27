package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import model.AuthData;
import dataAccess.*;
import model.UserData;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

// spark response object, spark request object

public class LoginHandler implements Route {

    private Gson gson = new Gson();
    private LoginService loginService;

    public LoginHandler(LoginService loginService) {
        this.loginService = loginService;
    }

    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                InputStream reqBody = exchange.getRequestBody();

                String reqData = Utils.readString(reqBody);

                UserData user = gson.fromJson(reqData, UserData.class);
                String authToken = loginService.login(user);
                AuthData authData = new AuthData(user.username(), authToken);
                String authTokenJson = gson.toJson(authData);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStream resBody = exchange.getResponseBody();
                resBody.write(authTokenJson.getBytes());
                resBody.close();

                success = true;
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
                String reqData = request.body();
                UserData user = gson.fromJson(reqData, UserData.class);
                String authToken = loginService.login(user);
                AuthData authData = new AuthData(user.username(), authToken);
                String authTokenJson = gson.toJson(authData);

                response.status(HttpURLConnection.HTTP_OK);
                return authTokenJson;
            } else {
                response.status(HttpURLConnection.HTTP_BAD_REQUEST);
                return errorResponse("Invalid request method");
            }
        } catch (DataAccessException e) {
            response.status(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return errorResponse(e.getMessage());
        }
    }

    private String errorResponse(String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        return gson.toJson(errorResponse);
    }
}
