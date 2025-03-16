package com.zhaw.frontier.exceptions;

/**
 * Exception for map loading errors.
 */
public class MapLoadingException extends Exception {

    /**
     * Constructor for the MapLoadingException.
     * @param message The message to be displayed.
     */
    public MapLoadingException(String message) {
        super(message);
    }
}
