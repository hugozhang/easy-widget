package me.about.widget.jobflow.entity;

import lombok.Data;

import java.util.List;


@Data
public class JobFlowDef {

    private String jobFlowId;

    private List<TaskNode> tasks;

}
