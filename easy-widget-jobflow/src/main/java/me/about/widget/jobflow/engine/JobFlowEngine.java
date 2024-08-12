package me.about.widget.jobflow.engine;

import me.about.widget.jobflow.entity.JobFlowDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JobFlowEngine {


    @Autowired
    private JobFlowRegistrar jobFlowRegistrar;

    @Autowired
    private JobFlowExecutor jobFlowExecutor;


    private void registerJobFlow(JobFlowDef jobFlowDef) {
        jobFlowRegistrar.registerJobFlow(jobFlowDef);
    }

    public void executeJobFlow(JobFlowDef jobFlowDef) {
        registerJobFlow(jobFlowDef);
        executeJobFlow(jobFlowDef.getJobFlowId());
    }

    public void executeJobFlow(String jobFlowId) {
        jobFlowExecutor.executeJobFlow(jobFlowId);
    }

}
