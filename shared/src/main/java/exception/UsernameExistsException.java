package exception;

public class UsernameExistsException extends ResponseException {

    public UsernameExistsException(String message) {
        super(403, message); // Conflict status code
    }
}
