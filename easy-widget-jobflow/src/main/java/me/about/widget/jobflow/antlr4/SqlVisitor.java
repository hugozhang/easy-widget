package me.about.widget.jobflow.antlr4;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.about.widget.jobflow.entity.JobFlowDef;
import me.about.widget.jobflow.entity.TaskNode;
import me.about.widget.jobflow.sql.antlr.JobFlowDSLBaseVisitor;
import me.about.widget.jobflow.sql.antlr.JobFlowDSLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// 访问者类
@Slf4j
public class SqlVisitor extends JobFlowDSLBaseVisitor<List<String>> {

    private final List<String> last = new ArrayList<>();

    @Getter
    private final JobFlowDef jobFlowDef = new JobFlowDef();

    private final Map<String, TaskNode> taskNodeMap = new HashMap<>();

    public SqlVisitor() {
        jobFlowDef.setTasks(new ArrayList<>());
    }

    @Override
    public List<String> visitJobFlow(JobFlowDSLParser.JobFlowContext context) {
        List<String> allTasks = new ArrayList<>();
        String jobFlowId = context.jobFlowId().getText();
        jobFlowDef.setJobFlowId(jobFlowId);
        for (JobFlowDSLParser.TaskSequenceContext taskSequence : context.jobBlock().taskSequences().taskSequence()) {
            List<String> tasks = visitTaskSequence(taskSequence);
            allTasks.addAll(tasks);
        }
        return allTasks;
    }

    @Override
    public List<String> visitTaskSequence(JobFlowDSLParser.TaskSequenceContext context) {
        List<String> result = new ArrayList<>();
        if(context.task() != null) {
            List<String> current = extracted(context);
            if (!last.isEmpty()) {
                log.debug("last:" + last);
                log.debug("current:" + current);
                for (String taskName : current) {
                    TaskNode currentTaskNode = taskNodeMap.get(taskName);
                    for (String lastTaskName : last) {
                        currentTaskNode.getDependsOn().add(lastTaskName);
                    }
                }
                last.clear();
            }
            result.addAll(current);
            last.addAll(result);
        }
        if (context.taskSequence() != null) {
            result.addAll(visitTaskSequence(context.taskSequence()));
        }
        return result;
    }

    private List<String> extracted(JobFlowDSLParser.TaskSequenceContext context ) {
        List<String> left = new ArrayList<>();
        if (context.task().taskName() != null) {
            left.addAll(visitTask(context.task()));
        } else if (context.task().taskGroup() != null) {
            left.addAll(visitTaskGroup(context.task().taskGroup()));
        }
        return left;
    }

    @Override
    public List<String> visitTask(JobFlowDSLParser.TaskContext context) {
        List<String> allTasks = new ArrayList<>();
        String taskName = context.getText();
        TaskNode taskNode = new TaskNode(taskName);
        taskNodeMap.put(taskName, taskNode);
        jobFlowDef.getTasks().add(taskNode);
        allTasks.add(taskName);
        return allTasks;
    }

    @Override
    public List<String> visitTaskGroup(JobFlowDSLParser.TaskGroupContext ctx) {
        return ctx
                .task()
                .stream()
                .map(this::visitTask)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    // 主方法，用于演示
    public static void main(String[] args) {

//        String input = "abc:={(1,2)->b->c->(e,f)}";
//        CharStream cs = CharStreams.fromString(input);
//        JobFlowDSLLexer lexer = new JobFlowDSLLexer(cs);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        JobFlowDSLParser parser = new JobFlowDSLParser(tokens);
//        SqlVisitor visitor = new SqlVisitor();
//        visitor.visit(parser.jobFlow());// 开始访问并获取任务列表
//        System.out.println(visitor.getJobFlowDef());

    }
}
