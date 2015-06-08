package com.cervantesvirtual.metadata;

/**
 * Exceptions raised buy the metadata package.
 *
 * @author RCC
 * @version 2011.03.10
 */
public class MetadataException extends Exception {

    static final long serialVersionUID = 1L;
    String command;

    public MetadataException(String message) {
        super(message);
    }

    public MetadataException(String command, String message) {
        super(message);
        this.command = command;
    }

    public String toString() {
        return "Metadata [" + command + "]: " + getMessage();
    }
}
