package service;

import dataAccess.DataAccessException;

import dataAccess.UserDAO;
import dataAccess.MemoryUserDAO;
import model.UserData;

import dataAccess.AuthDAO;
import dataAccess.MemoryAuthDAO;
import model.AuthData;

import java.util.List;
import java.util.Map;

public class RegistrationService {

    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegistrationService() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
    }

    public String register(UserData user) throws DataAccessException {
        if(userDAO.getUser(user.username()) != null) {
            throw new DataAccessException("Error: Username already exists");
        }
        userDAO.createUser(user);
        String authToken = authDAO.createAuth(user.username());
        return authToken;
    }
}
