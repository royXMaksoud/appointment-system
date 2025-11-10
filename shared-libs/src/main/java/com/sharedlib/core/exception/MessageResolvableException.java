package com.sharedlib.core.exception;

/**
 * Base exception that supports internationalized messages using i18n keys and dynamic arguments.
 * <p>
 * Extend this class for custom exceptions that require message resolution.
 */
public class MessageResolvableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * The i18n message key for this exception.
     */
    protected final String messageKey;

    /**
     * Arguments to be used for message formatting.
     */
    protected final Object[] args;

    /**
     * Constructs a MessageResolvableException with a message key.
     * @param messageKey the i18n message key
     */
    public MessageResolvableException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = null;
    }

    /**
     * Constructs a MessageResolvableException with a message key and arguments.
     * @param messageKey the i18n message key
     * @param args arguments for message formatting
     */
    public MessageResolvableException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }

    /**
     * Constructs a MessageResolvableException with a message key, arguments, and cause.
     * @param messageKey the i18n message key
     * @param cause the cause of the exception
     * @param args arguments for message formatting
     */
    public MessageResolvableException(String messageKey, Throwable cause, Object... args) {
        super(messageKey, cause);
        this.messageKey = messageKey;
        this.args = args;
    }

    /**
     * Returns the i18n message key for this exception.
     * @return message key
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Returns the arguments for message formatting.
     * @return arguments array
     */
    public Object[] getArgs() {
        return args;
    }
}
