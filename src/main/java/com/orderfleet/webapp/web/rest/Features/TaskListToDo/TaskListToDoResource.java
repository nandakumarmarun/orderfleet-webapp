package com.orderfleet.webapp.web.rest.Features.TaskListToDo;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Task;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.repository.TaskListRepository;
import com.orderfleet.webapp.repository.TaskRepository;
import com.orderfleet.webapp.web.rest.TaskListResource;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
public class TaskListToDoResource {

    private final Logger log = LoggerFactory.getLogger(TaskListResource.class);

    @Inject
    private TaskListRepository taskListRepository;

    @Inject
    private TaskRepository taskRepository;

    @Timed
    @RequestMapping(value = "/task-list-to-do", method = RequestMethod.GET)
    public String getDayPlans(Pageable pageable, Model model) throws URISyntaxException {
        log.debug("Web request to get a page of day plans not Assigned");
        return "company/DayPlanToDo";
    }


    @RequestMapping(value = "/task-list-to-do/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskListToDo>> getAllNotAssigedTasks(){
        log.debug("Entering Task To Do ");
        List<Task> allTasks =  taskRepository.findAllPidsCompanyId();
        log.debug("tasks  " + allTasks.size());

        List<Task> assignedTasks = taskListRepository.findAllByNotInTaskListPid();
        List<TaskListToDo> taskName= new ArrayList<>();
        log.debug("taskList  " + assignedTasks.size());
        allTasks.forEach(allTaskObj->{
            Optional<Task> optTask = assignedTasks.stream()
                    .filter(assignedTaskObj->allTaskObj.getId()
                            .equals(assignedTaskObj.getId())).findAny();
            if(!optTask.isPresent()){
                log.debug("taslk");
                TaskListToDo taskListToDo = new TaskListToDo();
                taskListToDo.setTaskName(allTaskObj.getAccountProfile().getName());
                taskListToDo.setActivityName(allTaskObj.getActivity().getName());
                taskName.add(taskListToDo);
            }
        });
        log.debug("Not Assigned Task Size : "+ taskName.size());
        return new ResponseEntity<>(taskName, HttpStatus.OK);
    }


}
