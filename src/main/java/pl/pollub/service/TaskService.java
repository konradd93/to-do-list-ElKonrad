package pl.pollub.service;

import pl.pollub.domain.Task;
import pl.pollub.domain.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by konrad on 25.07.17.
 */
public interface TaskService {

    Task saveTask(Task task);

    Task getTaskById(Long id);

    List<Task> getAllTasks();

    void deleteTaskById(Long id);

    void deleteTask(Task task);

    Set<Task> getTasksByOwnerId(Long ownerId);

    Set<User> getContributorsByTaskId(Long taskId);

    Task getTaskByTaskIdAndOwnerId(Long taskId, Long ownerId);

    Set<Task> getAllSharedTasksForUser(Long userId);

    Task updateTask(Task task);

    Task finishTask(Long taskId,Long userId);

    Map<Task,User> getAllFinishedTaskWithUsersWhoDoneThey(Long userId);

    Set<Task> getAllDoneSharedTasksForUser(Long userId);

    Set<Task> getDoneTasksByOwnerId(Long ownerId);
}
