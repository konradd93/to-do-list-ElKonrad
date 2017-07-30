package pl.pollub.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.pollub.exception.Error;
import pl.pollub.exception.*;

/**
 * Created by konrad on 30.07.17.
 */
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Error> taskNotFound(TaskNotFoundException e) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<Error>(error, e.getHttpReturnStatus());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Error> userNotFound(UserNotFoundException e) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<Error>(error, e.getHttpReturnStatus());
    }

    @ExceptionHandler(TaskForUserNotFoundException.class)
    public ResponseEntity<Error> taskForUserNotFound(TaskForUserNotFoundException e) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<Error>(error, e.getHttpReturnStatus());
    }

    @ExceptionHandler(UserUsernameExistException.class)
    public ResponseEntity<Error> usernameExist(UserUsernameExistException e ) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<Error>(error, e.getHttpReturnStatus());
    }

    @ExceptionHandler(SharedTaskForUserNotFoundException.class)
    public ResponseEntity<Error> sharedTasksForUserNotFound(SharedTaskForUserNotFoundException e) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<Error>(error, e.getHttpReturnStatus());
    }

    @ExceptionHandler(TaskCannotBeUpdatedException.class)
    public ResponseEntity<Error> taskCannotBeUpdated(TaskCannotBeUpdatedException e) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<Error>(error, e.getHttpReturnStatus());
    }
}
