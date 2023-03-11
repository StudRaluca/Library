package library.exceptions;

/**
 *  This exception is related to all objects, which are not currently in the database
 */
public class DatabaseObjectException extends Exception{

    public DatabaseObjectException() {
        super("This object was not found in the database");
    }

    public DatabaseObjectException(String message){
        super(message);
    }

    public DatabaseObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseObjectException(Throwable cause) {
        super(cause);
    }

    public DatabaseObjectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
