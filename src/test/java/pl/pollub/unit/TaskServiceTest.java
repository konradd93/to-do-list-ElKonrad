package pl.pollub.unit;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pollub.component.CustomMapper;
import pl.pollub.component.CustomMapperImpl;
import pl.pollub.domain.Task;
import pl.pollub.domain.User;
import pl.pollub.dto.NewTask;
import pl.pollub.exception.SharedTaskForUserNotFoundException;
import pl.pollub.exception.TaskForUserNotFoundException;
import pl.pollub.exception.TaskNotFoundException;
import pl.pollub.repository.InMemoryTaskRepository;
import pl.pollub.service.TaskService;
import pl.pollub.service.UserService;
import pl.pollub.service.impl.TaskServiceImpl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TaskListTest {

    @MockBean
    private UserService userService;

    private CustomMapper customMapper;
    private TaskService taskService;

    @Before
    public void setup(){
        customMapper = new CustomMapperImpl(new ModelMapper());
        taskService = new TaskServiceImpl(new InMemoryTaskRepository(), userService);
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

    @Test(expected = TaskNotFoundException.class)
    public void shouldThrowAnExceptionWhenRemoveNonexistentTaskById(){
        taskService.deleteTaskById(1L);
    }

    @Test(expected = TaskNotFoundException.class)
    public void shouldThrowAnExceptionWhenRemoveNonexistentTask(){
        Task task = new Task(1L, "Task");
        taskService.deleteTask(task);
    }

    @Test
    public void whenCreateTaskWithOwnerThenCanFindThisTaskByOwnerId() {
        User owner = new User(1L, "user1");
        Task task = new Task(1L, "Test1", true, owner, null);

        taskService.saveTask(task);
        when(userService.getUserById(anyLong())).thenReturn(owner);

        Set<Task> ownerTasks = taskService.getTasksByOwnerId(owner.getId());
        assertTrue(ownerTasks.contains(task));
    }

    @Test(expected = TaskForUserNotFoundException.class)
    public void shouldThrowExceptionWhenFindTaskByOwnerIdAndThisUserIsNotOwnerAnyTask() {
        User owner = new User(1L, "user1");
        User notOwner = new User(2L, "user2");
        Task task = new Task(1L, "Test1", true, owner, null);

        taskService.saveTask(task);
        when(userService.getUserById(anyLong())).thenReturn(notOwner);

        taskService.getTasksByOwnerId(notOwner.getId());
    }

    @Test
    public void whenCreateTaskWithContributorsThenForOneOfTheseContributorsThereIsATask() {
        User owner = new User(1L, "user1");
        User contributor1 = new User(2L, "user2");
        User contributor2 = new User(3L, "user3");
        Task task = new Task(1L, "Test1", true, owner, Sets.newHashSet(contributor1, contributor2));

        when(userService.getUserByUsername(contributor1.getUsername())).thenReturn(contributor1);
        when(userService.getUserByUsername(contributor2.getUsername())).thenReturn(contributor2);
        taskService.saveTask(task);

        Set<User> contributors = taskService.getContributorsByTaskId(task.getId());
        assertTrue(contributors.contains(contributor1));
        assertTrue(contributors.contains(contributor2));
    }

    @Test
    public void whenCreateTaskWithContributorsWithoutSettingUserIdThenThisContributorsAreOnListTask() {
        User owner = new User(1L, "user1");
        User contributor1 = new User(2L, "user2");
        User contributor2 = new User(3L, "user3");

        Task task = new Task(1L, "Test1", true, owner, Sets.newHashSet(new User(null, "user2"), new User(null, "user3")));

        when(userService.getUserByUsername(contributor1.getUsername())).thenReturn(contributor1);
        when(userService.getUserByUsername(contributor2.getUsername())).thenReturn(contributor2);
        taskService.saveTask(task);

        Set<User> contributors = taskService.getContributorsByTaskId(task.getId());
        assertTrue(contributors.contains(contributor1));
        assertTrue(contributors.contains(contributor2));
    }

    @Test
    public void whenCreateTaskThenCanFindItByTaskIdAndOwnerId() {
        User owner = new User(1L, "user1");
        User contributor1 = new User(2L, "user2");
        User contributor2 = new User(3L, "user3");
        Task task = new Task(1L, "Test1", true, owner, Sets.newHashSet(contributor1, contributor2));

        taskService.saveTask(task);

        Task foundTask = taskService.getTaskByTaskIdAndOwnerId(task.getId(), owner.getId());
        assertEquals(task, foundTask);
        assertEquals(task.getId(), foundTask.getId());
        assertEquals(task.getContent(), foundTask.getContent());
    }

    @Test
    public void whenCreateSharedTasksThenCanGetThisTasksForContributor() {
        User owner = new User(1L, "user1");
        User contributor1 = new User(2L, "user2");
        User contributor2 = new User(3L, "user3");
        Task task1 = new Task(1L, "Test1", true, owner, Sets.newHashSet(contributor1, contributor2));
        Task task2 = new Task(2L, "Test2", true, owner, Sets.newHashSet(contributor1));
        Task task3 = new Task(3L, "Test3", true, owner, Sets.newHashSet(contributor2));

        when(userService.getUserById(anyLong())).thenReturn(contributor2);
        taskService.saveTask(task1);
        taskService.saveTask(task2);
        taskService.saveTask(task3);

        Set<Task> allSharedTasksForContributor2 = taskService.getAllSharedTasksForUser(contributor2.getId());

        assertTrue(allSharedTasksForContributor2.contains(task1));
        assertFalse(allSharedTasksForContributor2.contains(task2));
        assertTrue(allSharedTasksForContributor2.contains(task3));
    }

    @Test(expected = SharedTaskForUserNotFoundException.class)
    public void shouldThrowExceptionWhenGetSharedTasksForContributorAndThereIsNoTaskForHim() {
        User owner = new User(1L, "user1");
        User contributor1 = new User(2L, "user2");
        User contributor2 = new User(3L, "user3");
        Task task1 = new Task(1L, "Test1", true, owner, Sets.newHashSet(contributor1));
        Task task2 = new Task(2L, "Test2", true, owner, Sets.newHashSet(contributor1));

        when(userService.getUserById(anyLong())).thenReturn(contributor1);
        taskService.saveTask(task1);
        taskService.saveTask(task2);

        taskService.getAllSharedTasksForUser(contributor2.getId());
    }

    @Test
    public void whenUpdateContentTaskThenThisTaskHasThatContent() {
        User owner = new User(1L, "user1");
        Task task = new Task(1L, "Test1", true, owner, null);
        taskService.saveTask(task);

        NewTask newTask = new NewTask("UpdatedContent", null);
        Task newTaskEntity = customMapper.mapToEntity(newTask);
        newTaskEntity.setId(task.getId());

        Task updatedTask = taskService.updateTask(newTaskEntity);
        assertEquals(task.getContent(), updatedTask.getContent());
    }

    @Test
    public void whenUpdateTaskByAddingContributorsThenContributorsShareThisTask() {
        User owner = new User(1L, "user1");
        User contributor1 = new User(2L, "user2");
        User contributor2 = new User(3L, "user3");
        Task task = new Task(1L, "Test1", true, owner, null);
        taskService.saveTask(task);

        NewTask newTask = new NewTask("UpdatedContent", Sets.newHashSet(new User(null, "user2"), new User(null, "user3")));
        Task newTaskEntity = customMapper.mapToEntity(newTask);
        newTaskEntity.setId(task.getId());

        when(userService.getUserByUsername(contributor1.getUsername())).thenReturn(contributor1);
        when(userService.getUserByUsername(contributor2.getUsername())).thenReturn(contributor2);
        Task updatedTask = taskService.updateTask(newTaskEntity);
        Set<User> contributors = updatedTask.getContributors().stream().collect(Collectors.toSet()); //without stream().collect(Collectors.toSet()) there is a problem with equals/hashcode for Set?

        assertNotNull(contributors);
        assertTrue(contributors.contains(contributor1));
        assertTrue(contributors.contains(contributor2));
    }

    @Test
    public void userCanSeeHisOwnAndSharedTasks(){

        when(userService.getUserById())
        taskService.getAllSharedTasksForUser();
    }
}



















