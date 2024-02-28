package server;

import com.google.gson.Gson;
import dataAccess.*;
import exception.ResponseException;
import spark.*;
import service.*;

import java.util.HashMap;
import java.util.Map;

public class Server {

    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    private LoginService loginService = new LoginService(userDAO, authDAO);
    private RegistrationService registrationService = new RegistrationService(userDAO, authDAO);
    private JoinService joinGameService = new JoinService(userDAO, authDAO, gameDAO);
    private ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private Gson gson = new Gson();

    public int run(int desiredPort) {

        System.out.println("Initializing HTTP Server on port " + desiredPort);
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", new RegisterHandler(registrationService));
        Spark.post("/session", new LoginHandler(loginService));
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.delete("/session", new LogoutHandler(loginService));
        Spark.post("/game", new CreateGameHandler(joinGameService));
        Spark.put("/game", new JoinGameHandler(joinGameService));
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.type("application/json");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        String json = gson.toJson(errorResponse);
        res.body(json);
    }

    public static void main(String[] args) {
        String portNumber = "8080";
        int port = Integer.parseInt(portNumber);
        new Server().run(port);
    }
}
