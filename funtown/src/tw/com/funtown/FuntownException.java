package tw.com.funtown;

/**
 * Represents an error condition specific to the Facebook SDK for Android.
 */
public class FuntownException extends RuntimeException {
    static final long serialVersionUID = 1;

    /**
     * Constructs a new FuntownException.
     */
    public FuntownException() {
        super();
    }

    /**
     * Constructs a new FuntownException.
     * 
     * @param message
     *            the detail message of this exception
     */
    public FuntownException(String message) {
        super(message);
    }

    /**
     * Constructs a new FuntownException.
     * 
     * @param message
     *            the detail message of this exception
     * @param throwable
     *            the cause of this exception
     */
    public FuntownException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new FuntownException.
     * 
     * @param throwable
     *            the cause of this exception
     */
    public FuntownException(Throwable throwable) {
        super(throwable);
    }
}