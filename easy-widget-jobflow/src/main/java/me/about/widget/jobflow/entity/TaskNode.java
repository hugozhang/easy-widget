package me.about.widget.jobflow.entity;


import lombok.Data;

import java.util.List;

@Data
public class TaskNode {

    //Task Bean Class  SimpleName
    private String taskId;

    //put taskId
    private List<String> dependsOn;

}
