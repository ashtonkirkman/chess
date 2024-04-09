package clientTests;

import exception.ResponseException;
import model.ListGameResponse;
import org.junit.jupiter.api.*;
import server.Server;
import client.ui.ServerFacade;

import java.io.IOException;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws ResponseException, IOException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() throws ResponseException, IOException {
        server.stop();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void testRegister() throws IOException, ResponseException {
        String authToken = serverFacade.register("bobby", "testpassword", "newemail");
        Assertions.assertNotNull(authToken);
        serverFacade.clear(authToken);
    }

    @Test
    public void testRegisterFailure() throws IOException, ResponseException {
        String authToken = serverFacade.register("existinguser", "existingpassword", "existing@example.com");
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.register("existinguser", "existingpassword", "existing@example.com");
        });
        serverFacade.clear(authToken);
    }

    @Test
    public void testLogin() throws IOException, ResponseException {
        String authToken = serverFacade.register("testuser", "testpassword", "testemail");
        authToken = serverFacade.login("testuser", "testpassword");
        Assertions.assertNotNull(authToken); // Check if authToken is not null
        serverFacade.clear(authToken);
    }

    @Test
    public void testLoginFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.login("nonexistentuser", "invalidpassword");
        });
    }

    @Test
    public void testLogout() throws IOException, ResponseException {
        String authToken = serverFacade.register("testuser", "testpassword", "testemail");
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.logout(authToken);
        });
        serverFacade.clear(authToken);
    }

    @Test
    public void testLogoutFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.logout("invalidAuthToken");
        });
    }

    @Test
    public void testCreateGame() throws IOException, ResponseException {
        String authToken = serverFacade.register("testuser", "testpassword", "testemail");
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.createGame(authToken, "Test Game");
        });
        serverFacade.clear(authToken);
    }

    @Test
    public void testCreateGameFailure() throws IOException, ResponseException {
        String authToken = serverFacade.register("testuser", "testpassword", "testemail");
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.createGame(authToken, null);
        });
        serverFacade.clear(authToken);
    }

    @Test
    public void testJoinGame() throws IOException, ResponseException {
        String authToken = serverFacade.register("hello", "hi", "hola");
        serverFacade.createGame(authToken, "Test Game");
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.joinGame(authToken, 1, null);
        });
        serverFacade.clear(authToken);
    }

    @Test
    public void testJoinGameFailure() throws IOException, ResponseException {
        String authToken = serverFacade.register("testuser", "testpassword", "testemail");
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame(authToken, 5, "WHITE");
        });
        serverFacade.clear(authToken);
    }

    @Test
    public void testObserveGame() throws IOException, ResponseException {
        String authToken = serverFacade.register("testuser", "testpassword", "testemail");
        serverFacade.createGame(authToken, "Test Game");
        Assertions.assertDoesNotThrow(() -> {
            serverFacade.observeGame(authToken, 1);
        });
        serverFacade.clear(authToken);
    }

    @Test
    public void testObserveGameFailure() throws IOException, ResponseException{
        String authToken = serverFacade.register("testuser", "testpassword", "testemail");
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.observeGame(authToken, 1111);
        });
        serverFacade.clear(authToken);
    }

    @Test
    public void testListGames() throws IOException, ResponseException {
        String authToken = serverFacade.register("testuser", "testpassword", "testemail");
        ListGameResponse gameResponse = serverFacade.listGames(authToken);
        Assertions.assertNotNull(gameResponse);
        serverFacade.clear(authToken);
    }

    @Test
    public void testListGamesFailure() {
        Assertions.assertThrows(ResponseException.class, () -> {
            serverFacade.listGames("invalid auth token");
        });
    }

}
