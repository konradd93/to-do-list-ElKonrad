package pl.pollub.unit;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import pl.pollub.component.CustomMapper;
import pl.pollub.component.CustomMapperImpl;
import pl.pollub.domain.Task;
import pl.pollub.domain.User;
import pl.pollub.repository.InMemoryTaskRepository;
import pl.pollub.repository.InMemoryUserRepository;
import pl.pollub.service.TaskService;
import pl.pollub.service.UserService;
import pl.pollub.service.impl.TaskServiceImpl;
import pl.pollub.service.impl.UserServiceImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class TaskServiceTest {

    @MockBean
    private UserService userService;

    private CustomMapper customMapper;
    private TaskService taskService;

    @Before
    public void setUp(){
        customMapper = new CustomMapperImpl(new ModelMapper());
        taskService = new TaskServiceImpl(new InMemoryTaskRepository(), userService);
    }

    @Test
    public void whenISaveTaskThenTaskExistInTaskList(){
        Task task = new Task(1L,"Task1");
        taskService.saveTask(task);

        assertTrue("Task not exist",taskService.getAllTasks().contains(task));
    }

    @Test
    public void whenISaveTaskThenICanGetThisById(){
        String taskContent="Task1";
        Task task = new Task(1L,taskContent);
        taskService.saveTask(task);

        assertEquals("Bad task content. Incorrect task found.",taskContent,taskService.getTaskById(task.getId()).getContent());
        assertEquals("Task not found",task,taskService.getTaskById(task.getId()));
    }

    @Test
    public void whenAndrewSharedTasksWithGregoryThenGeorgeSeeThisTasks(){
        User andrew = new User(1L, "Andrew");
        User gregory = new User(2L, "Gregory");;

        Task sharedTask1 = new Task(1L, "sharedTask1", true, andrew, Sets.newHashSet(gregory));
        Task sharedTask2 = new Task(2L, "sharedTask2", true, andrew, Sets.newHashSet(gregory));
        Task notSharedTask1 = new Task(3L, "notSharedTask1", true, andrew, Sets.newHashSet());
        taskService.saveTask(sharedTask1);
        taskService.saveTask(sharedTask2);
        taskService.saveTask(notSharedTask1);

        Set<Task> allSharedTasksForGorge = taskService.getAllSharedTasksForUser(gregory.getId());

        assertTrue("George doesn't see the tasks which Andrew shared with him",allSharedTasksForGorge.containsAll(Sets.newHashSet(sharedTask1,sharedTask2)));
        assertFalse("George sees the task which Andrew not shared with him", allSharedTasksForGorge.contains(notSharedTask1));
    }

    // ---1---
    @Test
    public void gregorySeesAllHisAndSharedWithAndrewTasks(){
        User gregory = new User(1L,"Gregory");
        User andrew = new User(2L,"Andrew");

        when(userService.getUserById(1L)).thenReturn(gregory);

        //private Gregory tasks
        Task privateTask1 = new Task(1L,"Task1",true,gregory,Sets.newHashSet());
        Task privateTask2 = new Task(2L,"Task2",true,gregory,Sets.newHashSet());
        taskService.saveTask(privateTask1);
        taskService.saveTask(privateTask2);
        assertTrue("Gregory doesn't see his private tasks", taskService.getTasksByOwnerId(1L).containsAll(Arrays.asList(privateTask1, privateTask2)));

        //Gregory task shared with Andrew
        Task gregoryTaskSharedWithAndrew = new Task(3L, "Task3", true, gregory, Sets.newHashSet(andrew));
        taskService.saveTask(gregoryTaskSharedWithAndrew);
        assertTrue("Gregory doesn't see his private task shared with Andrew", taskService.getTasksByOwnerId(gregory.getId()).contains(gregoryTaskSharedWithAndrew));
        assertTrue("Andrew doesn't see task shared with Gregory", taskService.getAllSharedTasksForUser(andrew.getId()).contains(gregoryTaskSharedWithAndrew));

        //Andrew task shared with Gregory
        Task andrewTaskSharedWithGregory = new Task(4L, "Task4", true, andrew, Sets.newHashSet(gregory));
        taskService.saveTask(andrewTaskSharedWithGregory);
        assertTrue("Gregory doesn't see Andrew task shared with him", taskService.getAllSharedTasksForUser(gregory.getId()).contains(andrewTaskSharedWithGregory));

        //Gregory see all task (own and shared)
        assertTrue(taskService.getAllSharedAndOwnTasksByUserId(gregory.getId()).containsAll(Sets.newHashSet(privateTask1, privateTask2, gregoryTaskSharedWithAndrew, andrewTaskSharedWithGregory)));
    }

    // ---2---
    @Test
    public void whenGregoryCreateOrIsAContribitorActiveTasks_ThenHeCanSeeAllActiveTasksForUser() {
        User gregory = new User(1L,"Gregory");
        User alex = new User(2L,"Alex");
        Task task1 = new Task(1L,"Task1",true,gregory,Sets.newHashSet());
        Task task2 = new Task(2L,"Task2",true,alex,Sets.newHashSet(gregory));
        taskService.saveTask(task1);
        taskService.saveTask(task2);

        assertTrue(taskService.getActiveTasksByUserId(gregory.getId()).containsAll(Sets.newHashSet(task1,task2)));
    }

    @Test
    public void whenOwnerOrContributorCompletesTaskThenTheTaskDissapearsFromTasksList(){
        User john = new User(1L,"Gregory");
        User alex = new User(2L,"Alex");
        Task task1 = new Task(1L,"Task1",true,john,Sets.newHashSet());
        Task task2 = new Task(2L,"Task2",true,alex,Sets.newHashSet(john));
        taskService.saveTask(task1);
        taskService.saveTask(task2);

        //task 1
        taskService.markTheTaskAsDoneByTaskId(task1.getId());

        assertFalse("Task not removed from taskList, probably this task is active.",
                taskService.getActiveTasksByUserId(john.getId()).contains(task1));

        assertTrue("Task not added to done, probably this task is active(not completed).",
                taskService.getCompletedTasksByUserId(john.getId()).contains(task1));

        //task 2
        taskService.markTheTaskAsDoneByTaskId(task2.getId());
        assertFalse("Task not removed from taskList, probably this task is active.",
                taskService.getActiveTasksByUserId(john.getId()).contains(task2));

        assertFalse("Task not removed from taskList, probably this task is active.",
                taskService.getActiveTasksByUserId(alex.getId()).contains(task2));

        assertTrue("Task not added to done, probably this task is active(not completed)."
                ,taskService.getCompletedTasksByUserId(john.getId()).contains(task2));

    }


}
