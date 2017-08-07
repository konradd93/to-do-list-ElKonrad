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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        if (task.getContributors() != null && ifContributorsIdAreNotSetting(task.getContributors()))
            task = setIdForContributors(task);

        task.setActive(true);
        return taskRepository.save(task);
    }

    private boolean ifContributorsIdAreNotSetting(Set<User> contributors) {
        return contributors.stream().anyMatch(e -> e.getId() == null);
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
        return Optional.ofNullable(taskRepository.findTaskByOwnerId(ownerId)).orElseThrow(() -> new TaskForUserNotFoundException(ownerId));
    }

    @Override
    public Set<Task> getDoneTasksByOwnerId(Long ownerId) {
        userService.getUserById(ownerId);
        return Optional.ofNullable(taskRepository.findDoneTasksByOwnerId(ownerId)).orElseThrow(() -> new TaskForUserNotFoundException(ownerId));
    }

    @Override
    public Set<User> getContributorsByTaskId(Long taskId) {
        return taskRepository.findContributorsByTaskId(taskId);
    }

    @Override
    public Task getTaskByTaskIdAndOwnerId(Long taskId, Long ownerId) {
        return taskRepository.findTaskByTaskIdAndOwnerId(taskId, ownerId);
    }

    @Override
    public Set<Task> getAllSharedTasksForUser(Long userId) {
        userService.getUserById(userId);
        return Optional.ofNullable(taskRepository.findAllSharedTasksForUser(userId)).orElseThrow(() -> new SharedTaskForUserNotFoundException(userId));
    }

    @Override
    public Set<Task> getAllDoneSharedTasksForUser(Long userId) {
        userService.getUserById(userId);
        return Optional.ofNullable(taskRepository.findAllDoneSharedTasksForUser(userId)).orElseThrow(() -> new SharedTaskForUserNotFoundException(userId));
    }

    @Override
    public Task updateTask(Task task) {
        Long taskId = task.getId();
        if (task.getContributors() != null && ifContributorsIdAreNotSetting(task.getContributors()))
            task = setIdForContributors(task);
        Task updatedTask = Optional.ofNullable(taskRepository.update(task)).orElseThrow(() -> new TaskCannotBeUpdatedException(taskId));
        return updatedTask;
    }

    @Override
    public Task finishTask(Long taskId,Long userId) {
        Task finishedTask = Optional.ofNullable(taskRepository.findOne(taskId)).orElseThrow(() -> new TaskCannotBeUpdatedException(taskId));
        finishedTask.setActive(false);
        finishedTask.setWhoFinish(userService.getUserById(userId));
        return finishedTask;
    }

    @Override
    public Map<Task, User> getAllFinishedTaskWithUsersWhoDoneThey(Long userId) {
        Set<Task> allTaskForUser = taskRepository.findTaskByOwnerId(userId);
        allTaskForUser.addAll(taskRepository.findAllSharedTasksForUser(userId));
        return allTaskForUser.stream().filter(task -> !task.isActive()).collect(Collectors.toMap(Function.identity(),Task::getWhoFinish));
    }


    private Task setIdForContributors(Task task) {
        task.getContributors().stream().forEach(e -> {
            User contributor = userService.getUserByUsername(e.getUsername());
            e.setId(contributor.getId());
        });

        return task;
    }
}
