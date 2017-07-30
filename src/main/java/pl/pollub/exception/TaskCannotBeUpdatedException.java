package pl.pollub.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Created by konrad on 30.07.17.
 */
@Getter
public class TaskCannotBeUpdatedException extends RuntimeException {

    private long taskId;

    public TaskCannotBeUpdatedException(Long taskId) {
        this.taskId = taskId;
    }

    public String getMessage(){
        return "Task [" + this.taskId + "] cannot be updated";
    }

    public HttpStatus getHttpReturnStatus(){
        return HttpStatus.CONFLICT;
    }
}
