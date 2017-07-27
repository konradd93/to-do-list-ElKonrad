package pl.pollub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.pollub.domain.Task;
import pl.pollub.exception.TaskNotFoundException;
import pl.pollub.repository.InMemoryTaskRepository;

import java.util.List;
import java.util.Optional;

/**
 * Created by konrad on 25.07.17.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private InMemoryTaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(InMemoryTaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task saveTask(Task entity) {
        return taskRepository.save(entity);
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
}
