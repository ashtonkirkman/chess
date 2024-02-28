package exception;

public class EmptyCredentialsException extends ResponseException {

    public EmptyCredentialsException(String message) {
        super(400, message); // Bad request status code
    }
}
