package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.UserData;
import service.LoginService;
import dataAccess.*;
import service.RegistrationService;

public class LoginHandlerTest {
    public static void main(String[] args) {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        // Create a mock login service (replace this with actual implementation)
        LoginService loginService = new LoginService(userDAO, authDAO);
        RegistrationService registrationService = new RegistrationService(userDAO, authDAO);

        // Example JSON string
        String json = "{\"username\": \"testUser\", \"password\": \"testPassword\", \"email\": \"testEmail\"}";

//        GsonBuilder builder = new GsonBuilder();
//        // Create Gson instance
//        Gson gson = builder.create();
        Gson gson = new Gson();

        try {
            // Convert JSON string to UserData object
            UserData user = gson.fromJson(json, UserData.class);
            System.out.println(user);

            // Call loginService.login(user)
            String authToken = registrationService.register(user);
            AuthData authData = authDAO.getAuth(authToken);

            String authDataJson = gson.toJson(authData);
            System.out.println(authDataJson);

            // Output the result
//            System.out.println("Result from loginService.login(user): " + authTokenJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}