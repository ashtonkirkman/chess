package dataAccess;

import model.AuthData;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO{
    private List<AuthData> authTokens;

    public MemoryAuthDAO() {
        this.authTokens = new ArrayList<AuthData>();
    }
    public String createAuth(String username) throws DataAccessException {
        for (AuthData a : authTokens) {
            if (a.username().equals(username)) {
                throw new DataAccessException("Error: Username already logged in");
            }
        }
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        authTokens.add(auth);
        return authToken;
    }
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData a : authTokens) {
            if (a.authToken().equals(authToken)) {
                return a;
            }
        }
        throw new DataAccessException("Error: Auth token not found");
    }
    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            authTokens.removeIf(a -> a.authToken().equals(authToken));
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
