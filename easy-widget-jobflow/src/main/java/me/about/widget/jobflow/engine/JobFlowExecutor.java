package me.about.widget.jobflow.engine;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import me.about.widget.jobflow.core.dag.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class JobFlowExecutor {


    @Autowired
    private JobFlowRegistrar jobFlowRegistrar;

    private final Executor executor;

    public JobFlowExecutor() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.executor = new ThreadPoolExecutor(2, availableProcessors, 10, TimeUnit.SECONDS
                , new LinkedBlockingQueue<>(200)
                , new ThreadFactoryBuilder().setNameFormat("Job Flow Thread").build()
                , (r, executor) -> {
            log.warn("[PUT Cache Thread] rejectedExecution:{}", r);
            r.run();
        });
    }

    public void executeJobFlow(String jobFlowId) {
        Graph graph = jobFlowRegistrar.getGraph(jobFlowId);
        if (graph == null) {
            throw new RuntimeException("jobFlowId not found");
        }
        log.info("[JobFlowExecutor Start] executeJobFlow : {}", jobFlowId);
        Graph copyGraph = Graph.of(graph);
        copyGraph.executeGraph(executor);
        log.info("[JobFlowExecutor End] executeJobFlow : {},graph : {}", jobFlowId,copyGraph);
    }

}
