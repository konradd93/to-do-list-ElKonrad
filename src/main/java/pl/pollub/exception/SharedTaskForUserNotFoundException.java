package pl.pollub.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by konrad on 30.07.17.
 */
@Getter
public class SharedTaskForUserNotFoundException extends RuntimeException {

    private long userId;

    public SharedTaskForUserNotFoundException(long userId) {
        this.userId = userId;
    }

    public String getMessage(){
        return "Shared tasks for user [" + this.userId + "] are not exist";
    }

    public HttpStatus getHttpReturnStatus(){
        return HttpStatus.NOT_FOUND;
    }
}
