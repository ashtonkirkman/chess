package service;

import dataAccess.DataAccessException;

import dataAccess.UserDAO;
import dataAccess.MemoryUserDAO;
import exception.UnauthorizedException;
import model.UserData;

import dataAccess.AuthDAO;
import dataAccess.MemoryAuthDAO;

public class LoginService {

    private UserDAO userDAO;
    private AuthDAO authDAO;

    public LoginService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String login(String username, String password) throws DataAccessException {
        UserData userFromDB = userDAO.getUser(username, password);
        if(userFromDB == null) {
            throw new DataAccessException("Error: Username does not exist, please register");
        }
        String authToken = authDAO.createAuth(username);
        return authToken;
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        try {
            authDAO.deleteAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Error: Unauthorized");
        }
    }

}
