package ru.yandex.practicum.filmorate.exception;

// @ResponseStatus(HttpStatus.BAD_REQUEST) можно так поставить но мне кажется в хендлере проще статусы проставлять
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}