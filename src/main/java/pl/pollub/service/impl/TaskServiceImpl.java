package pl.pollub.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pollub.domain.Task;
import pl.pollub.domain.User;
import pl.pollub.exception.SharedTaskForUserNotFoundException;
import pl.pollub.exception.TaskCannotBeUpdatedException;
import pl.pollub.exception.TaskForUserNotFoundException;
import pl.pollub.exception.TaskNotFoundException;
import pl.pollub.repository.InMemoryTaskRepository;
import pl.pollub.service.TaskService;
import pl.pollub.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by konrad on 25.07.17.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private InMemoryTaskRepository taskRepository;

    private UserService userService;

    @Autowired
    public TaskServiceImpl(InMemoryTaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @Override
    public Task saveTask(Task task) {

        if (task.getContributors() != null)
            task = addContributors(task);

        task.setActive(true);
        return taskRepository.save(task);
    }

    @Override
    public Task getTaskById(Long id) {
        Task task = Optional.ofNullable(taskRepository.findOne(id)).orElseThrow(() -> new TaskNotFoundException(id));
        return task;
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void deleteTaskById(Long id) {
        Optional.ofNullable(taskRepository.findOne(id)).orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(id);
    }

    @Override
    public void deleteTask(Task task) {
        Long taskId = task.getId();
        Optional.ofNullable(taskRepository.findOne(taskId)).orElseThrow(() -> new TaskNotFoundException(taskId));
        taskRepository.delete(task);
    }

    @Override
    public Set<Task> getTasksByOwnerId(Long ownerId) {
        userService.getUserById(ownerId);
        Set<Task> ownerTasks = Optional.ofNullable(taskRepository.findTaskByOwnerId(ownerId)).orElseThrow(() -> new TaskForUserNotFoundException(ownerId));
        return ownerTasks;
    }

    @Override
    public Set<User> getContributorsByTaskId(Long id) {
        return taskRepository.findContributorsByTaskId(id);
    }

    @Override
    public Task getTaskByTaskIdAndOwnerId(Long taskId, Long ownerId) {
        return taskRepository.findTaskByTaskIdAndOwnerId(taskId, ownerId);
    }

    @Override
    public Set<Task> getAllSharedTasksForUser(Long userId) {
        userService.getUserById(userId);
        Set<Task> sharedTasks = Optional.ofNullable(taskRepository.findAllSharedTasksForUser(userId)).orElseThrow(() -> new SharedTaskForUserNotFoundException(userId));
        return sharedTasks;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = Optional.ofNullable(taskRepository.update(addContributors(task))).orElseThrow(() -> new TaskCannotBeUpdatedException(task.getId()));
        return updatedTask;
    }

    private Task addContributors(Task task) {
        task.getContributors().stream().forEach(e -> {
            User contributor = userService.getUserByUsername(e.getUsername());
            e.setId(contributor.getId());
        });

        return task;
    }
}
