package pl.pollub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.UriComponentsBuilder;
import pl.pollub.component.CustomMapper;
import pl.pollub.domain.Task;
import pl.pollub.dto.NewTask;
import pl.pollub.exception.Error;
import pl.pollub.exception.TaskNotFoundException;
import pl.pollub.service.TaskService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private CustomMapper customMapper;

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Task> addTask(@RequestBody NewTask newTask,
                                        UriComponentsBuilder uriComponentsBuilder) {

        Task savedTask = taskService.saveTask(customMapper.mapToEntity(newTask));
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = uriComponentsBuilder.path("/api/tasks")
                .path(String.valueOf(savedTask.getId()))
                .build()
                .toUri();

        return new ResponseEntity<Task>(savedTask, headers, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Task>> getAllTasks() {

        List<Task> taskList = taskService.getAllTasks();
        return new ResponseEntity<List<Task>>(taskList, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        Task task = taskService.getTaskById(id);
        return new ResponseEntity<Task>(task, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> removeTask(@PathVariable Long id){

        taskService.deleteTaskById(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Error> taskNotFound(TaskNotFoundException e) {
        Error error = new Error(e.getMessage());
        return new ResponseEntity<Error>(error, e.getHttpReturnStatus());
    }
}