package me.about.widget.jobflow.engine;

import me.about.widget.jobflow.annotation.DependsOn;
import me.about.widget.jobflow.annotation.JobFlow;
import me.about.widget.jobflow.core.Task;
import me.about.widget.jobflow.core.dag.Graph;
import me.about.widget.jobflow.core.dag.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
public class JobFlowContext {


    @Autowired
    private ApplicationContext applicationContext;

    Map<String, Graph> graphs = new ConcurrentHashMap<>();

    Map<String, List<Task>> taskNodes = new ConcurrentHashMap<>();

    Map<String, List<Task>> taskDependencies = new ConcurrentHashMap<>();


    @Autowired
    Map<String, Task> beansOfTask;

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

//        Map<String, Task> beans = applicationContext.getBeansOfType(Task.class);

        beansOfTask.forEach((beanName,task) -> {
            JobFlow jobFlow = task.getClass().getAnnotation(JobFlow.class);
            if (jobFlow != null) {
                String jobFlowId = jobFlow.id();
                addJobFlowNode(jobFlowId,task);

                DependsOn dependsOn = task.getClass().getAnnotation(DependsOn.class);
                if (dependsOn != null) {
                    Class<? extends Task>[] depends = dependsOn.value();
                    for (Class<? extends Task> depend : depends) {
                        Task dependBean =  beansOfTask.get(StringUtils.uncapitalize(depend.getSimpleName()));
                        addDependency(beanName, dependBean);
                    }
                }
            }
        });

        taskNodes.forEach((key, value) -> {
            int v = value.size();
            Graph graph = new Graph(v);
            value.forEach(task -> {
                String beanName = getTaskBeanName(task);
                List<Task> tasks = taskDependencies.get(beanName);
                if (tasks != null) {
                    tasks.forEach(dependency -> {
                        String dependencyBeanName = getTaskBeanName(dependency);
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

    private String getTaskBeanName(Task task) {
        return StringUtils.uncapitalize(task.getClass().getSimpleName());
    }

}
