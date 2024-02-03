package pw.react.backend.exceptions;

public class FlatValidationException extends RuntimeException {
    private final String resourcePath;

    public FlatValidationException(String message, String resourcePath) {
        super(message);
        this.resourcePath = resourcePath;
    }

    public FlatValidationException(String message) {
        this(message, null);
    }

    public String getResourcePath() {
        return resourcePath;
    }
}
