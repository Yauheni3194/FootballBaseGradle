package by.kovalevskiy.FootballBaseGradle.exception;

public class GameAlreadyExistsException extends RuntimeException{
    public GameAlreadyExistsException(String message) {
        super(message);
    }
}
