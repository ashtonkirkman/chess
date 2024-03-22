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

    @Test
    public void testLogin() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertNotNull(authToken); // Check if authToken is not null
    }

    @Test
    public void testLoginFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.login("nonexistentuser", "invalidpassword");
        });
    }

    @Test
    public void testLogout() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.logout(authToken);
        });
    }

    @Test
    public void testLogoutFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.logout("invalidAuthToken");
        });
    }

    @Test
    public void testCreateGame() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.createGame(authToken, "Test Game");
        });
    }

    @Test
    public void testCreateGameFailure() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.createGame(authToken, null);
        });
    }

    // Positive test for joinGame method
    @Test
    public void testJoinGame() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.joinGame(authToken, 5, null);
        });
    }

    // Negative test for joinGame method
    @Test
    public void testJoinGameFailure() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame(authToken, 5, "WHITE");
        });
    }

    // Positive test for observeGame method
    @Test
    public void testObserveGame() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.observeGame(authToken, 5);
        });
    }

    // Negative test for observeGame method
    @Test
    public void testObserveGameFailure() throws IOException, ResponseException{
        String authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.observeGame(authToken, 1111);
        });
    }

    @Test
    public void testListGames() throws IOException, ResponseException {
        String authToken = serverFacade.login("testuser", "testpassword");
        ListGameResponse gameResponse = serverFacade.listGames(authToken);
        Assertions.assertNotNull(gameResponse);
    }

    // Negative test for listGames method
    @Test
    public void testListGamesFailure() throws IOException, ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.listGames("invalid auth token");
        });
    }

}
