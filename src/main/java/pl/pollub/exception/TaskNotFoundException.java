package pl.pollub.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by konrad on 25.07.17.
 */
@Getter
public class TaskNotFoundException extends RuntimeException {

    private long taskId;

    public TaskNotFoundException(long taskId) {
        this.taskId = taskId;
    }

    public String getMessage(){
        return "Task [" + this.taskId + "] is not exist";
    }

    public HttpStatus getHttpReturnStatus(){
        return HttpStatus.NOT_FOUND;
    }
}
