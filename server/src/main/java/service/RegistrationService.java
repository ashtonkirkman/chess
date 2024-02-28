package service;

import dataAccess.DataAccessException;

import dataAccess.UserDAO;
import dataAccess.MemoryUserDAO;
import exception.EmptyCredentialsException;
import exception.UsernameExistsException;
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

    public String register(UserData user) throws DataAccessException, EmptyCredentialsException, UsernameExistsException {
        if(user.username() == null || user.password() == null || user.username().isEmpty() || user.password().isEmpty()) {
            throw new EmptyCredentialsException("Error: Username and password cannot be empty");
        }
        if(userDAO.getUser(user.username(), user.password()) != null) {
            throw new UsernameExistsException("Error: Username already exists");
        }
        userDAO.createUser(user);
        String authToken = authDAO.createAuth(user.username());
        return authToken;
    }
}
