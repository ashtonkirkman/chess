package server;

import com.google.gson.Gson;
import dataAccess.*;
import exception.ResponseException;
import server.websocket.WebSocketHandler;
import spark.*;
import service.*;

import java.util.HashMap;
import java.util.Map;

public class Server {

    public UserDAO userDAO;
    public AuthDAO authDAO;
    public GameDAO gameDAO;
    private LoginService loginService;
    private RegistrationService registrationService;
    private JoinService joinGameService;
    private ClearService clearService;
    private Gson gson = new Gson();
    private WebSocketHandler webSocketHandler;

    public Server(){
        try {
            authDAO = new MySqlAuthDAO();
            userDAO = new MySqlUserDAO();
            gameDAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            System.out.printf(e.getMessage());
        }
        loginService = new LoginService(userDAO, authDAO);
        registrationService = new RegistrationService(userDAO, authDAO);
        joinGameService = new JoinService(userDAO, authDAO, gameDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {

        System.out.println("Initializing HTTP Server on port " + desiredPort);
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/connect", webSocketHandler);

        Spark.post("/user", new RegisterHandler(registrationService));
        Spark.post("/session", new LoginHandler(loginService));
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.delete("/session", new LogoutHandler(loginService));
        Spark.post("/game", new CreateGameHandler(joinGameService));
        Spark.put("/game", new JoinGameHandler(joinGameService));
        Spark.get("/game", new ListGamesHandler(joinGameService));
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.statusCode());
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
