package pl.pollub.exception;

import lombok.Getter;

/**
 * Created by konrad on 25.07.17.
 */
@Getter
public class TaskNotFoundException extends RuntimeException {

    private long taskId;

    public TaskNotFoundException(long taskId) {
        this.taskId = taskId;
    }
}
