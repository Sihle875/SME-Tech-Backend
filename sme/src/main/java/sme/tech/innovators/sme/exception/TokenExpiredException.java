package sme.tech.innovators.sme.exception;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) { super(message); }
}
