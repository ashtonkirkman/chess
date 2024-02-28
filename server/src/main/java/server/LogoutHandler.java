package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import exception.ResponseException;
import exception.UnauthorizedException;
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
            String authToken = request.headers("Authorization");
            loginService.logout(authToken);
            return "{}";
        } catch (UnauthorizedException e) {
            throw new ResponseException(e.statusCode(), e.getMessage());
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
