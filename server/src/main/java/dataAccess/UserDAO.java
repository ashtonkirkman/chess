package dataAccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username, String password) throws DataAccessException;
    void clear() throws DataAccessException;
}