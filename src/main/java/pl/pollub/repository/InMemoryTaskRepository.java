package pl.pollub.repository;

import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;
import pl.pollub.domain.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Objects.requireNonNull;

/**
 * Created by konrad on 25.07.17.
 */
@Component
public class InMemoryTaskRepository {

    private AtomicLong counter = new AtomicLong();

    private List<Task> fakeTable = new LinkedList<>();

    public Task save(Task task){
        requireNonNull(task);
        task.setId(generateId());
        fakeTable.add(task);
        return task;
    }

    public Task findOne(Long id){
        if(!fakeTable.stream().anyMatch(e -> e.getId().equals(id)))
            return null;
        return fakeTable.stream().filter(e -> e.getId() == id).findFirst().get();
    }

    public List<Task> findAll(){
        return fakeTable;
    }

    public void delete(Long id){
        Task task = findOne(id);
        fakeTable.remove(task);
    }

    public void delete(Task task){
        fakeTable.remove(task);
    }

    private Long generateId() {
        return counter.incrementAndGet();
    }
}
