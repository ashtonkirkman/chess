package clientTests;

import exception.ResponseException;
import model.ListGameResponse;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.io.IOException;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:8080");
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testRegister() throws IOException, ResponseException {
        String authToken = serverFacade.register("testuser", "testpassword", "test@example.com");
        Assertions.assertNotNull(authToken); // Check if authToken is not null
    }

    @Test
    public void testRegisterFailure() throws IOException, ResponseException {
        serverFacade.register("existinguser", "existingpassword", "existing@example.com");
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.register("existinguser", "existingpassword", "existing@example.com");
        });
    }

    // Positive test for login method
    @Test
    public void testLogin() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertNotNull(authToken); // Check if authToken is not null
    }

    // Negative test for login method
    @Test
    public void testLoginFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.login("nonexistentuser", "invalidpassword");
        });
    }

    // Positive test for logout method
    @Test
    public void testLogout() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        serverFacade.logout(authToken); // Assuming validAuthToken is a valid token
        // If no exception is thrown, logout was successful
    }

    // Negative test for logout method
    @Test
    public void testLogoutFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.logout("invalidAuthToken");
        });
    }

    // Positive test for createGame method
    @Test
    public void testCreateGame() throws IOException, ResponseException {
        serverFacade.createGame("validAuthToken", "Test Game");
        // If no exception is thrown, game creation was successful
    }

    // Negative test for createGame method
    @Test
    public void testCreateGameFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.createGame("invalidAuthToken", "Invalid Game");
        });
    }

    // Positive test for joinGame method
    @Test
    public void testJoinGame() throws IOException, ResponseException {
        serverFacade.joinGame("validAuthToken", 1234, "white");
        // If no exception is thrown, joining game was successful
    }

    // Negative test for joinGame method
    @Test
    public void testJoinGameFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame("invalidAuthToken", 5678, "black");
        });
    }

    // Positive test for observeGame method
    @Test
    public void testObserveGame() throws IOException, ResponseException {
        serverFacade.observeGame("validAuthToken", 1234);
        // If no exception is thrown, observing game was successful
    }

    // Negative test for observeGame method
    @Test
    public void testObserveGameFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.observeGame("invalidAuthToken", 5678);
        });
    }

    // Positive test for listGames method
    @Test
    public void testListGames() throws IOException, ResponseException {
        ListGameResponse gameResponse = serverFacade.listGames("validAuthToken");
        Assertions.assertNotNull(gameResponse); // Check if response is not null
        // Additional assertions can be made based on the response data
    }

    // Negative test for listGames method
    @Test
    public void testListGamesFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.listGames("invalidAuthToken");
        });
    }

}
