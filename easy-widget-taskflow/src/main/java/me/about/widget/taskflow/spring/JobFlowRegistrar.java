package me.about.widget.taskflow.spring;

import me.about.widget.taskflow.annotation.DependsOn;
import me.about.widget.taskflow.annotation.JobFlow;
import me.about.widget.taskflow.dag.Graph;
import me.about.widget.taskflow.dag.Node;
import me.about.widget.taskflow.core.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class JobFlowRegistrar {

    @Autowired
    private ApplicationContext applicationContext;

    Map<String, Graph> graphs = new HashMap<>();

    Map<String, List<Task>> taskNodes = new HashMap<>();

    Map<String, List<Task>> taskDependencies = new HashMap<>();

    public Graph getGraph(String jobFlowId) {
        return graphs.get(jobFlowId);
    }

    public void addJobFlowNode(String jobFlowId, Task task) {
        taskNodes.computeIfAbsent(jobFlowId, k -> new ArrayList<>()).add(task);
    }

    public void addDependency(String key, Task task) {
        taskDependencies.computeIfAbsent(key, k -> new ArrayList<>()).add(task);
    }

    @PostConstruct
    public void registerTaskFlows() {

        Map<String, Task> beans = applicationContext.getBeansOfType(Task.class);

        beans.forEach((beanName,task) -> {
            JobFlow jobFlow = task.getClass().getAnnotation(JobFlow.class);
            if (jobFlow != null) {
                String groupName = jobFlow.value();
                addJobFlowNode(groupName,task);

                DependsOn dependsOn = task.getClass().getAnnotation(DependsOn.class);
                if (dependsOn != null) {
                    Class<? extends Task>[] depends = dependsOn.value();
                    for (Class<? extends Task> depend : depends) {
                        Task dependBean =  beans.get(StringUtils.uncapitalize(depend.getSimpleName()));
                        addDependency(beanName, dependBean);
                    }
                }
            }
        });

        taskNodes.forEach((key, value) -> {
            int v = value.size();
            Graph graph = new Graph(v);
            value.forEach(task -> {
                String beanName = getBeanName(task);
                List<Task> tasks = taskDependencies.get(beanName);
                if (tasks != null) {
                    tasks.forEach(dependency -> {
                        String dependencyBeanName = getBeanName(dependency);
                        graph.addEdge(new Node(dependencyBeanName,dependency), new Node(beanName,task)); // A -> B
                    });
                } else {
                    graph.addEdge(new Node(beanName,task),null); // A -> nil
                }
            });
            // 检查环
            graph.topologicalSort();

            graphs.put(key, graph);
        });


    }

    private String getBeanName(Task task) {
        return StringUtils.uncapitalize(task.getClass().getSimpleName());
    }
}
