package me.jspider.base.bean;

/**
 * Overall unchecked exception.
 */
public class SpiderRuntimeException extends RuntimeException {
    public SpiderRuntimeException() { }

    public SpiderRuntimeException(String message) {
        super(message);
    }

    public SpiderRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpiderRuntimeException(Throwable cause) {
        super(cause);
    }

    public SpiderRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
