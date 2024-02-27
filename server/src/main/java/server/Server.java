package server;

import com.sun.net.httpserver.*;
import dataAccess.*;
import spark.*;
import service.*;

public class Server {

    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    private LoginService loginService = new LoginService(userDAO, authDAO);
    private RegistrationService registrationService = new RegistrationService(userDAO, authDAO);
    private JoinService joinGameService = new JoinService(userDAO, authDAO, gameDAO);
    private ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);

    public int run(int desiredPort) {

        System.out.println("Initializing HTTP Server on port " + desiredPort);
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", new RegisterHandler(registrationService));
        Spark.post("/session", new LoginHandler(loginService));
        Spark.delete("/db", new ClearHandler(clearService));
        Spark.delete("/session", new LogoutHandler(loginService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static void main(String[] args) {
        String portNumber = "8080";
        int port = Integer.parseInt(portNumber);
        new Server().run(port);
    }
}
