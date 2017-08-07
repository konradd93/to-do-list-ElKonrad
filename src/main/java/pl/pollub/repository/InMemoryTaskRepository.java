package pl.pollub.repository;

import org.springframework.stereotype.Component;
import pl.pollub.domain.Task;
import pl.pollub.domain.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Created by konrad on 25.07.17.
 */
@Component
public class InMemoryTaskRepository {

    private AtomicLong counter = new AtomicLong();

    private List<Task> tasksEntity = new LinkedList<>();

    public Task save(Task task){
        requireNonNull(task);
        task.setId(generateId());
        tasksEntity.add(task);
        return task;
    }

    public Task findOne(Long id){
        if(!tasksEntity.stream().anyMatch(e -> e.getId().equals(id)))
            return null;
        return tasksEntity.stream().filter(e -> e.getId() == id).findFirst().get();
    }

    public List<Task> findAll(){
        return tasksEntity;
    }

    public void delete(Long id){
        Task task = findOne(id);
        tasksEntity.remove(task);
    }

    public void delete(Task task){
        findOne(task.getId());
        tasksEntity.remove(task);
    }

    public Set<Task> findTaskByOwnerId(Long ownerId){
        Set<Task> ownerTasks = tasksEntity.stream().filter(e -> Objects.equals(e.getOwner().getId(), ownerId)).collect(Collectors.toSet());
        return ownerTasks.isEmpty() ? null : ownerTasks;
    }

    public Set<Task> findDoneTasksByOwnerId(Long ownerId){
        Set<Task> ownerTasks = tasksEntity.stream().filter(e -> Objects.equals(e.getOwner().getId(), ownerId) && e.isActive()).collect(Collectors.toSet());
        return ownerTasks.isEmpty() ? null : ownerTasks;
    }

    public Set<User> findContributorsByTaskId(Long id) {
        Set<User> contributors = new HashSet<>();
        tasksEntity.stream()
                .filter(e -> e.getId() == id)
                .forEach(e -> e.getContributors()
                        .stream()
                        .forEach(e1 -> contributors.add(e1)));
        return contributors;
    }

    public Task findTaskByTaskIdAndOwnerId(Long taskId, Long ownerId) {
        Set<Task> ownerTasks = findTaskByOwnerId(ownerId);
        Task task = ownerTasks.stream().filter(e -> e.getId() == taskId).findFirst().get();
        return task;
    }

    public Set<Task> findAllSharedTasksForUser(Long userId) {

        Set<Task> tasks = new HashSet<>();

        tasksEntity
                .forEach(e -> e.getContributors()
                            .forEach(e1 -> {
                                if (Objects.equals(e1.getId(), userId))
                                    tasks.add(e);
                            })
                );
        return tasks.isEmpty() ? null : tasks;
    }

    public Set<Task> findAllDoneSharedTasksForUser(Long userId) {

        Set<Task> tasks = new HashSet<>();

        tasksEntity
                .forEach(e -> e.getContributors()
                        .forEach(e1 -> {
                            if (Objects.equals(e1.getId(), userId) && e.isActive())
                                tasks.add(e);
                        })
                );
        return tasks.isEmpty() ? null : tasks;
    }

    public Task update(Task task) {
        if (tasksEntity.stream().noneMatch(e -> e.getId() == task.getId()))
            return null;
        Task actualTask = tasksEntity.stream().filter(e -> e.getId() == task.getId()).findFirst().get();
        actualTask.setContent(Optional.ofNullable(task.getContent()).orElse(actualTask.getContent()));
        actualTask.setContributors(Optional.ofNullable(task.getContributors()).orElse(actualTask.getContributors()));
        return actualTask;
    }

    private Long generateId() {
        return counter.incrementAndGet();
    }
}
