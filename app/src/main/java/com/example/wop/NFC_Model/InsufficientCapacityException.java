
package com.example.wop.NFC_Model;

public class InsufficientCapacityException extends Exception {

    public InsufficientCapacityException() {
    }

    public InsufficientCapacityException(String message) {
        super(message);
    }

    public InsufficientCapacityException(StackTraceElement[] stackTraceElements) {
        setStackTrace(stackTraceElements);
    }
}
