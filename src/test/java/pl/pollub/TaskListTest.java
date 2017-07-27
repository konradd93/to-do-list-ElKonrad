package pl.pollub;

import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.pollub.component.CustomMapper;
import pl.pollub.component.CustomMapperImpl;
import pl.pollub.domain.Task;
import pl.pollub.dto.NewTask;
import pl.pollub.exception.TaskNotFoundException;
import pl.pollub.repository.InMemoryTaskRepository;
import pl.pollub.service.TaskService;
import pl.pollub.service.TaskServiceImpl;

import java.util.List;

import static org.junit.Assert.*;

public class TaskListTest {

    @MockBean
    private CustomMapper customMapper;
    @MockBean
    private TaskService taskService;

    @Before
    public void setup(){
        customMapper = new CustomMapperImpl(new ModelMapper());
        taskService = new TaskServiceImpl(new InMemoryTaskRepository());
    }

    @Test
    public void testDTO_NewTask_toEntityMapper(){
        NewTask newTask = new NewTask("New task test");
        Task task = customMapper.mapToEntity(newTask);

        assertEquals(newTask.getContent(), task.getContent());
        assertNull(task.getId());
    }

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
    public void ICanRemoveExistingTaskById(){
        //given: a task
        Task createdTask1 = taskService.saveTask(new Task(1L, "Test1"));
        Task createdTask2 = taskService.saveTask(customMapper.mapToEntity(new NewTask("Test2")));

        //when: i remove it
        taskService.deleteTaskById(createdTask1.getId());

        //then: it disappears
        List<Task> taskList = taskService.getAllTasks();
        assertFalse(taskList.contains(createdTask1));
    }

    @Test
    public void canRemoveExistingTask(){
        //given: a task
        Task createdTask1 = taskService.saveTask(new Task(1L, "Test1"));
        Task createdTask2 = taskService.saveTask(customMapper.mapToEntity(new NewTask("Test2")));

        //when: i remove it
        taskService.deleteTask(createdTask1);

        //then: it disappears
        List<Task> taskList = taskService.getAllTasks();
        assertFalse(taskList.contains(createdTask1));
    }

    @Test(expected = TaskNotFoundException.class)
    public void shouldThrowAnExceptionWhenGetByIdNonexistentTask(){
        taskService.getTaskById(1L);
    }

    @Test
    public void difference_between_newly_added_two_tasks_should_be_equal_one(){
        NewTask newTask1 = new NewTask("Task1");
        NewTask newTask2 = new NewTask("Task2");
        Task task1 = taskService.saveTask(customMapper.mapToEntity(newTask1));
        Task task2 = taskService.saveTask(customMapper.mapToEntity(newTask2));

        long difference = Math.abs(task2.getId() - task1.getId());
        assertEquals(1, difference);
    }

    @Test(expected = TaskNotFoundException.class)
    public void shouldThrowAnExceptionWhenRemoveNonexistentTaskById(){
        taskService.deleteTaskById(1L);
    }

    @Test(expected = TaskNotFoundException.class)
    public void shouldThrowAnExceptionWhenRemoveNonexistentTask(){
        Task task = new Task(1L, "Task");
        taskService.deleteTask(task);
    }
}