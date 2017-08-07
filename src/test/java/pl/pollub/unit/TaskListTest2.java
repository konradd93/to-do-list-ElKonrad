package pl.pollub.unit;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import pl.pollub.domain.Task;
import pl.pollub.domain.User;
import pl.pollub.repository.InMemoryTaskRepository;
import pl.pollub.repository.InMemoryUserRepository;
import pl.pollub.service.UserService;
import pl.pollub.service.impl.UserServiceImpl;

import javax.validation.constraints.AssertTrue;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskServiceImplTest {

    @Mock
    private InMemoryTaskRepository taskRepository;

    @Mock
    private InMemoryUserRepository userRepository;

    @Mock
    private UserService userService;

    @Before
    public void setUp() {
        taskRepository = new InMemoryTaskRepository();
        userRepository = new InMemoryUserRepository();
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    public void whenISaveTaskThenTaskExistInTaskRepository(){
        Task task = new Task(1L,"asdafaf");
        taskRepository.save(task);

        assertTrue("Task not exist",taskRepository.findAll().contains(task));
    }

    @Test
    public void georgeSeesAllHisAndSharedWithAndrewTasks(){
        User george = new User();
        User andrew = new User();
        userRepository.save(george);
        userRepository.save(andrew);

        //George tasks
        Task privateTask1 = new Task(1L,"Task1",false,george,null);
        Task privateTask2 = new Task(2L,"Task2",false,george,null);
        taskRepository.save(privateTask1);
        taskRepository.save(privateTask2);
        assertTrue("Gorge doesn't see his private tasks", taskRepository.findAll().containsAll(Arrays.asList(privateTask1, privateTask2)));

        //George task shared with Andrew
        Task georgeTaskSharedWithAndrew = new Task(3L, "Task3", false, george, Sets.newHashSet(andrew));
        taskRepository.save(georgeTaskSharedWithAndrew);
        assertTrue("Gorge doesn't see his private task shared with Andrew", taskRepository.findAll().contains(georgeTaskSharedWithAndrew));

        //Andrew task shared with George
        Task andrewTasksharedWithGeorge = new Task(4L, "Task4", false, andrew, Sets.newHashSet(george));
        taskRepository.save(andrewTasksharedWithGeorge);
        assertTrue("Gorge doesn't see Andrew task shared with him", taskRepository.findAll().contains(andrewTasksharedWithGeorge));

    }


}
