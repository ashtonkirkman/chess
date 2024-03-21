package ui;
import model.AuthData;
import model.LoginRequest;
import com.google.gson.Gson;
import model.UserData;
import server.Server;

import java.io.IOException;

public class ServerFacade {
    private ClientCommunicator communicator;
    private final String serverUrl;

    public ServerFacade(String url) {
        this.communicator = new ClientCommunicator();
        this.serverUrl = url;
    }

    public String register(String username, String password, String email) throws IOException {
        String urlString = serverUrl + "/user";
        UserData user = new UserData(username, password, email);
        String requestBody = new Gson().toJson(user);
        String responseBody = communicator.doPost(urlString, requestBody);
        AuthData authData = new Gson().fromJson(responseBody, AuthData.class);
//        return authData.authToken();
        return responseBody;
    }


    public String login(String username, String password) throws IOException {
        LoginRequest request = new LoginRequest(username, password);
        String urlString = serverUrl + "/session";
        String requestBody = new Gson().toJson(request);
        String responseBody = communicator.doPost(urlString, requestBody);
        AuthData authData = new Gson().fromJson(responseBody, AuthData.class);
        return authData.authToken();
    }

    public String logout(String authToken) throws IOException {
        String urlString = serverUrl + "/session";
        return communicator.doDelete(urlString, authToken);
    }

    public static void main(String[] args) {
        String portNumber = "8080";
        int port = Integer.parseInt(portNumber);
        new Server().run(port);
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        try {
            System.out.println(serverFacade.register("user", "password", "email"));
            String authToken = serverFacade.login("user", "password");
            System.out.println(authToken);
            System.out.println(serverFacade.logout(authToken));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
