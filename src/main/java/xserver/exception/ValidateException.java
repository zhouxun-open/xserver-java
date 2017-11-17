package xserver.exception;

public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidateException(String message) {
        super(message);
    }

    public ValidateException(Throwable cause) {
        super(cause);
    }

    public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }
}
