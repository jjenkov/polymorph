package com.plmph.util;

public class PolymorphException extends RuntimeException {

    public PolymorphException() {
        super();
    }

    public PolymorphException(String message) {
        super(message);
    }

    public PolymorphException(String message, Throwable cause) {
        super(message, cause);
    }

    public PolymorphException(Throwable cause) {
        super(cause);
    }

    protected PolymorphException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
