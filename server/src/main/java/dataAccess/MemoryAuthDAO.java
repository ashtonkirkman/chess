package dataAccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    private Map<String, AuthData> authTokens;

    public MemoryAuthDAO() {
        this.authTokens = new HashMap<>();
    }
    public void createAuth(AuthData auth) throws DataAccessException {
        if (authTokens.containsKey(auth.authToken())) {
            throw new DataAccessException("Error: Auth Token already exists");
        }
        for (AuthData a : authTokens.values()) {
            if (a.username().equals(auth.username())) {
                throw new DataAccessException("Error: Username already logged in");
            }
        }
        try {
            authTokens.put(auth.authToken(), auth);
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to create auth token");
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Error: Auth Token \"" + authToken + "\" not found");
        }
        return authTokens.get(authToken);
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            authTokens.remove(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to delete auth token");
        }
    }

    public void clear() throws DataAccessException {
        try {
            authTokens.clear();
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to clear data");
        }
    }
}
