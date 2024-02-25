package dataAccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {

    private Map<String, UserData> users;

    public MemoryUserDAO() {
        this.users = new HashMap<>();
    }

    public void createUser(UserData user) throws DataAccessException {
        try {
            if (users.containsKey(user.username())) {
                throw new DataAccessException("Error: Username already exists");
            }
            users.put(user.username(), user);
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to create user");
        }
    }

    // Example method to retrieve a user by username from the data store
    public UserData getUser(String username) throws DataAccessException {
        try {
            if (!users.containsKey(username)) {
                throw new DataAccessException("User with name " + username + " not found");
            }
            return users.get(username);
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to get user");
        }
    }

    public void clear() throws DataAccessException {
        try {
            users.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to clear data");
        }
    }
}
