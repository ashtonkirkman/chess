package service;

import dataAccess.DataAccessException;

import dataAccess.UserDAO;
import dataAccess.MemoryUserDAO;
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

    public String login(UserData user) throws DataAccessException {
        String username = user.username();
        UserData userFromDB = userDAO.getUser(username);
        if(userFromDB == null) {
            throw new DataAccessException("Error: Username does not exist, please register");
        }
        String authToken = authDAO.createAuth(user.username());
        return authToken;
    }

    public void logout(String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }

}
