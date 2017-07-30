package pl.pollub.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by konrad on 29.07.17.
 */
@Getter
public class UserNotFoundException extends RuntimeException {

    private long userId;

    public UserNotFoundException() {
    }

    public UserNotFoundException(long userId) {
        this.userId = userId;
    }

    public String getMessage(){
        return "User [" + this.userId + "] is not exist";
    }

    public HttpStatus getHttpReturnStatus(){
        return HttpStatus.NOT_FOUND;
    }
}
