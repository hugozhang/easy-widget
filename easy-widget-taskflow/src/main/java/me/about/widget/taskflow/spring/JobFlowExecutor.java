package me.about.widget.taskflow.spring;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import me.about.widget.taskflow.dag.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class JobFlowExecutor {


    @Autowired
    private JobFlowRegistrar jobFlowRegistrar;

    private final Executor executor;

    public JobFlowExecutor() {
        this.executor = new ThreadPoolExecutor(5, 20, 10, TimeUnit.SECONDS
                , new LinkedBlockingQueue<>(200)
                , new ThreadFactoryBuilder().setNameFormat("Job Flow Thread").build()
                , (r, executor) -> {
//            log.error("[PUT Cache Thread] rejectedExecution:{}", r);
            r.run();
        });
    }

    public void executeJobFlow(String jobFlowId) {
        Graph graph = jobFlowRegistrar.getGraph(jobFlowId);
        if (graph == null) {
            throw new RuntimeException("jobFlowId not found");
        }
        graph.getStages().forEach(stage -> {
            stage.executeStage(executor);
        });
    }

    public void printJobFlow(String jobFlowId) {
        Graph graph = jobFlowRegistrar.getGraph(jobFlowId);
        if (graph == null) {
            throw new RuntimeException("jobFlowId not found");
        }
        System.out.println(graph);
    }

}
