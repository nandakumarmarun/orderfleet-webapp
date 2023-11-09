package com.orderfleet.webapp.web.rest.Features.TaskListToDo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

public class TaskListToDo {
    private String TaskName;
    private String activityName;

    public String getTaskName() {
        return TaskName;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
}
