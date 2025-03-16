package com.zhaw.frontier.exceptions;

/**
 * This exception is thrown when an entity is not found.
 */
public class EntityNotFoundException extends Exception {

    /**
     * Constructor for the EntityNotFoundException.
     * @param message The message to be displayed.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
