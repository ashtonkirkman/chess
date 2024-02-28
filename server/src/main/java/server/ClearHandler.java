package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dataAccess.DataAccessException;
import exception.ResponseException;
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
            clearService.clear();
            return "{}";
        } catch (Exception e) {
            throw new ResponseException(401, e.getMessage());
        }
    }
}
