package pl.pollub.task;

import org.junit.Test;
import org.modelmapper.ModelMapper;
import pl.pollub.component.CustomMapper;
import pl.pollub.component.CustomMapperImpl;
import pl.pollub.domain.NewTask;
import pl.pollub.domain.Task;
import pl.pollub.exception.TaskNotFoundException;
import pl.pollub.repository.InMemoryTaskRepository;
import pl.pollub.service.TaskService;
import pl.pollub.service.TaskServiceImpl;

import java.util.List;

import static org.junit.Assert.*;

public class TaskListTest {

    private CustomMapper customMapper = new CustomMapperImpl(new ModelMapper());
    private TaskService taskService = new TaskServiceImpl(new InMemoryTaskRepository());

    @Test
    public void whenICreateNewTaskThenThisTaskIsOnTheTaskList() throws Exception {

        Task createdTask1 = taskService.saveTask(new Task(1L, "Test1"));
        Task createdTask2 = taskService.saveTask(customMapper.mapToEntity(new NewTask("Test2")));

        List<Task> taskList = taskService.getAllTasks();

        assertEquals("Test1", taskService.getTaskById(1L).getContent());
        assertTrue(taskList.contains(createdTask1));
        assertTrue(taskList.contains(createdTask2));
    }

    @Test
    public void ICanRemoveExistingTask(){
        //given: a task
        Task createdTask1 = taskService.saveTask(new Task(1L, "Test1"));
        Task createdTask2 = taskService.saveTask(customMapper.mapToEntity(new NewTask("Test2")));

        //when: i remove it
        taskService.deleteTask(createdTask1.getId());

        //then: it disappears
        List<Task> taskList = taskService.getAllTasks();
        assertFalse(taskList.contains(createdTask1));
    }

    @Test(expected = TaskNotFoundException.class)
    public void shouldThrowAnExceptionWhenGetByIdNonexistentTask(){
        taskService.getTaskById(1L);
    }
}