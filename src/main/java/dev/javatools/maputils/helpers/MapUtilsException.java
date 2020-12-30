package dev.javatools.maputils.helpers;

public class MapUtilsException extends RuntimeException {
    public MapUtilsException(Throwable throwable) {
        super(throwable);
    }

    public MapUtilsException(String message) {
        super(message);
    }
}
