package main;

/**
 * Created by chris_000 on 3/15/2016.
 */
public class IncorrectNumberOfInputsException extends RuntimeException{
    public IncorrectNumberOfInputsException() {
    }

    public IncorrectNumberOfInputsException(String message) {
        super(message);
    }
}
