package me.about.widget.jobflow.engine;

import me.about.widget.jobflow.core.Task;
import me.about.widget.jobflow.core.dag.Graph;
import me.about.widget.jobflow.core.dag.Node;
import me.about.widget.jobflow.entity.JobFlowDef;
import me.about.widget.jobflow.entity.TaskNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class JobFlowRegistrar {

    @Autowired
    private JobFlowContext jobFlowContext;

    public Graph getGraph(String jobFlowId) {
        return jobFlowContext.getGraph(jobFlowId);
    }

    public Graph registerJobFlow(JobFlowDef jobFlowDef) {
        if (jobFlowDef == null
                || jobFlowDef.getTasks() == null
                || jobFlowDef.getTasks().isEmpty()
                || StringUtils.isBlank(jobFlowDef.getJobFlowId())) {
            throw new IllegalArgumentException("jobFlowDef is error");
        }

//        if (jobFlowContext.graphs.containsKey(jobFlowDef.getJobFlowId())) {
//            throw new IllegalArgumentException("jobflowId has repeated!");
//        }

        Set<String> taskIds = new HashSet<>();
        List<TaskNode> taskNodes = jobFlowDef.getTasks();
        for (TaskNode taskNode : taskNodes) {
            if (StringUtils.isBlank(taskNode.getTaskId())) {
                throw new IllegalArgumentException("Task id : " + taskNode.getTaskId() + " is null or empty");
            }
            taskIds.add(taskNode.getTaskId());
            if (taskNode.getDependsOn() != null && !taskNode.getDependsOn().isEmpty()) {
                for (String dependsOn : taskNode.getDependsOn()) {
                    if (StringUtils.isBlank(dependsOn)) {
                        throw new IllegalArgumentException("Task id : " + taskNode.getTaskId() + ", dependsOn has null or empty");
                    }
                }
                taskIds.addAll(taskNode.getDependsOn());
            } else {
                taskNode.setDependsOn(new ArrayList<>());
            }
        }

        for (String taskId : taskIds) {
            Task task = jobFlowContext.beansOfTask.get(taskId);
            if (task == null) {
                throw new IllegalArgumentException("bean not found, taskId : " + taskId);
            }
        }

        Graph graph = new Graph(taskIds.size());

        for (TaskNode taskNode : taskNodes) {
            Task task = jobFlowContext.beansOfTask.get(taskNode.getTaskId());
            if (taskNode.getDependsOn() != null && !taskNode.getDependsOn().isEmpty()) {
                for (String dependsOn : taskNode.getDependsOn()) {
                    Task dependTask = jobFlowContext.beansOfTask.get(dependsOn);
                    Node parentNode = new Node(taskNode.getTaskId(), task);
                    Node dependNode = new Node(dependsOn, dependTask);
                    graph.addVertex(parentNode);
                    graph.addVertex(dependNode);
                    graph.addEdge(dependNode, parentNode); // A -> B
                }
            } else {
                graph.addVertex(new Node(taskNode.getTaskId(),task)); // A -> B
            }

        }

        // 检查环
        graph.topologicalSort();

        return graph;
    }
}
