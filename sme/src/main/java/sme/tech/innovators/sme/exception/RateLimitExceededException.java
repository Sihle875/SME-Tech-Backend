package sme.tech.innovators.sme.exception;

public class RateLimitExceededException extends RuntimeException {
    private final String attemptType;

    public RateLimitExceededException(String message, String attemptType) {
        super(message);
        this.attemptType = attemptType;
    }

    public String getAttemptType() { return attemptType; }
}
