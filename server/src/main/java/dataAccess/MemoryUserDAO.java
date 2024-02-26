package dataAccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{

    private Map<String, UserData> users;

    public MemoryUserDAO() {
        this.users = new HashMap<>();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("Error: Username already exists");
        }
        for (UserData u : users.values()) {
            if (u.email().equals(user.email())) {
                throw new DataAccessException("Error: Email already in use");
            }
        }
        users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!users.containsKey(username)) {
            return null;
        }
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            users.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to clear data");
        }
    }
}
