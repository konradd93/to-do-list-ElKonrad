package pl.pollub.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by konrad on 30.07.17.
 */
@Getter
public class UserUsernameExistException extends RuntimeException {

    private String username;

    public UserUsernameExistException(String username) {
        this.username = username;
    }

    public String getMessage(){
        return "User with that [" + this.username + "] username already exist";
    }

    public HttpStatus getHttpReturnStatus(){
        return HttpStatus.CONFLICT;
    }
}
