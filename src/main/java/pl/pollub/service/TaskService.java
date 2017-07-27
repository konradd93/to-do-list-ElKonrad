package pl.pollub.service;

import pl.pollub.domain.Task;

import java.util.List;

/**
 * Created by konrad on 25.07.17.
 */
public interface TaskService {

    Task saveTask(Task entity);

    Task getTaskById(Long id);

    List<Task> getAllTasks();

    void deleteTaskById(Long id);

    void deleteTask(Task task);
}
