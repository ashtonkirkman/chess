package dataAccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO {
    private Map<String, AuthData> authTokens;

    public MemoryAuthDAO() {
        this.authTokens = new HashMap<>();
    }
    public void createAuth(AuthData auth) throws DataAccessException {
        try {
            authTokens.put(auth.authToken(), auth);
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to create auth token");
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        try {
            return authTokens.get(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to get auth token");
        }
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            authTokens.remove(authToken);
        } catch (Exception e) {
            throw new DataAccessException("Error: Failed to delete auth token");
        }
    }
}
