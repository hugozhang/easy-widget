package me.about.widget.jobflow.engine;

import me.about.widget.jobflow.antlr4.SqlParser;
import me.about.widget.jobflow.core.dag.Graph;
import me.about.widget.jobflow.entity.JobFlowDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JobFlowEngine {


    @Autowired
    private JobFlowRegistrar jobFlowRegistrar;

    @Autowired
    private JobFlowExecutor jobFlowExecutor;


    public Graph registerJobFlow(JobFlowDef jobFlowDef) {
        return jobFlowRegistrar.registerJobFlow(jobFlowDef);
    }

    public void executeJobFlow(JobFlowDef jobFlowDef) {
        executeJobFlow(jobFlowDef.getJobFlowId());
    }

    public JobFlowDef parseJobFlow(String expression) {
        return SqlParser.builder().parse(expression);
    }

    public void executeJobFlow(Graph graph) {
        jobFlowExecutor.executeJobFlow(graph);
    }
    public void executeJobFlow(String jobFlowId) {
        jobFlowExecutor.executeJobFlow(jobFlowId);
    }

}
