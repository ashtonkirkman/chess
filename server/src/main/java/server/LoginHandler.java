package server;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import service.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

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

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String reqData = request.body();
            UserData user = gson.fromJson(reqData, UserData.class);
            String authToken = loginService.login(user.username(), user.password());
            AuthData authData = new AuthData(authToken, user.username());
            String authTokenJson = gson.toJson(authData);
            return authTokenJson;
        } catch (DataAccessException e) {
            throw new ResponseException(401, e.getMessage());
        }
    }
}
