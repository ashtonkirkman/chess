package server;

import com.google.gson.Gson;
import exception.EmptyCredentialsException;
import exception.ResponseException;
import exception.UsernameExistsException;
import model.AuthData;
import model.UserData;
import service.RegistrationService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.net.HttpURLConnection;


public class RegisterHandler implements Route {

    private Gson gson = new Gson();
    private RegistrationService registrationService;

    public RegisterHandler(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            String reqData = request.body();
            UserData user = gson.fromJson(reqData, UserData.class);
            String authToken = registrationService.register(user);
            AuthData authData = new AuthData(authToken, user.username());
            String authTokenJson = gson.toJson(authData);
            return authTokenJson;
        }catch (EmptyCredentialsException | UsernameExistsException e) {
            throw new ResponseException(e.StatusCode(), e.getMessage());
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
