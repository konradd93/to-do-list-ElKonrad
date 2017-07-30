package pl.pollub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import pl.pollub.component.CustomMapper;
import pl.pollub.domain.Task;
import pl.pollub.domain.User;
import pl.pollub.dto.NewTask;
import pl.pollub.dto.UserCreateForm;
import pl.pollub.service.TaskService;
import pl.pollub.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * Created by konrad on 29.07.17.
 */
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    private CustomMapper customMapper;

    private UserService userService;

    private TaskService taskService;

    @Autowired
    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<User> createUser(@RequestBody UserCreateForm userCreateForm,
                                           UriComponentsBuilder uriComponentsBuilder) {

        User savedUser = userService.save(customMapper.mapToEntity(userCreateForm));
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = uriComponentsBuilder.path("/api/users/")
                .path(String.valueOf(savedUser.getId()))
                .build()
                .toUri();
        headers.setLocation(locationUri);

        return new ResponseEntity<User>(savedUser, headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> getAllUsers() {

        List<User> userList = userService.getAllUsers();
        return new ResponseEntity<List<User>>(userList, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ResponseEntity<User> getUserById(@PathVariable Long userId){
        User user = userService.getUserById(userId);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeUser(@PathVariable Long userId){

        userService.deleteUserById(userId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    /* ***User adds own tasks*** */

    @RequestMapping(value = "/{userId}/tasks", method = RequestMethod.POST)
    public ResponseEntity<Task> addTaskForUser(@PathVariable Long userId,
                                        @RequestBody NewTask newTask,
                                        UriComponentsBuilder uriComponentsBuilder){
        User owner = userService.getUserById(userId);
        Task task = customMapper.mapToEntity(newTask);
        task.setOwner(owner);

        Task savedTask = taskService.saveTask(task);
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = uriComponentsBuilder.path("/api/users/")
                .path(String.valueOf(userId))
                .path("/tasks/")
                .path(String.valueOf(savedTask.getId()))
                .build()
                .toUri();
        headers.setLocation(locationUri);

        return new ResponseEntity<Task>(savedTask, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "{userId}/tasks", method = RequestMethod.GET)
    public ResponseEntity<Set<Task>> getAllUserTasks(@PathVariable Long userId) {
        Set<Task> ownerTasks = taskService.getTasksByOwnerId(userId);
        return new ResponseEntity<Set<Task>>(ownerTasks, HttpStatus.OK);
    }

    @RequestMapping(value = "{userId}/tasks/{taskId}", method = RequestMethod.GET)
    public ResponseEntity<Task> getUserTaskById(@PathVariable Long userId,
                                                @PathVariable Long taskId) {

        Task task = taskService.getTaskByTaskIdAndOwnerId(taskId, userId);
        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    @RequestMapping(value = "{userId}/tasks/shared", method = RequestMethod.GET)
    public ResponseEntity<Set<Task>> getAllSharedTasksForUser(@PathVariable Long userId) {

        Set<Task> sharedTasks = taskService.getAllSharedTasksForUser(userId);
        return new ResponseEntity<Set<Task>>(sharedTasks, HttpStatus.OK);
    }

    @RequestMapping(value = "{userId}/tasks/{taskId}", method = RequestMethod.PUT)
    public ResponseEntity<Task> addContributorsToTask(@PathVariable Long userId,
                                                      @PathVariable Long taskId,
                                                      @RequestBody NewTask newTask,
                                                      UriComponentsBuilder uriComponentsBuilder) {
        Task task = customMapper.mapToEntity(newTask);
        task.setId(taskId);
        Task updatedTask = taskService.updateTask(task);

        HttpHeaders headers = new HttpHeaders();
        URI locationUri = uriComponentsBuilder.path("/api/users/")
                .path(String.valueOf(userId))
                .path("/tasks/")
                .path(String.valueOf(updatedTask.getId()))
                .build()
                .toUri();
        headers.setLocation(locationUri);

        return new ResponseEntity<Task>(updatedTask, headers, HttpStatus.OK);
    }
}