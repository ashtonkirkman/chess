package service;

import dataAccess.DataAccessException;

import dataAccess.UserDAO;
import dataAccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import dataAccess.AuthDAO;
import dataAccess.MemoryAuthDAO;

public class RegistrationService {

    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegistrationService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String register(UserData user) throws DataAccessException {
        if(userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("Error: Username already exists");
        }
        userDAO.createUser(user);
        String authToken = authDAO.createAuth(user.username());
        return authToken;
    }

    public UserData getUser(String authToken) throws DataAccessException {
        AuthData authData = authDAO.getAuth(authToken);
        String username = authData.username();
        UserData user = userDAO.getUser(username);
        return user;
    }
}
