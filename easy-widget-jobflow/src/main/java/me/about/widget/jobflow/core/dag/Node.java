package me.about.widget.jobflow.core.dag;

import lombok.Getter;
import lombok.Setter;
import me.about.widget.jobflow.core.Task;
import me.about.widget.jobflow.enums.NodeStatus;

import java.util.List;
import java.util.Objects;


@Getter
public class Node {

    private final String nodeId;

    private final Task task;

    @Setter
    private NodeStatus status;

    @Setter
    private long elapsed;

    @Setter
    private List<Task> dependencies;

    public Node(String nodeId,Task task) {
        this.nodeId = nodeId;
        this.task = task;
        setStatus(NodeStatus.PENDING);
    }

    public void executeNode() {
        long startedAt = System.currentTimeMillis();
        setStatus(NodeStatus.RUNNING);
        getTask().execute();
        setStatus(NodeStatus.COMPLETED);
        long elapsed = System.currentTimeMillis() - startedAt;
        setElapsed(elapsed);
    }

    public void addDependency(Task task) {
        this.dependencies.add(task);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(nodeId, node.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId);
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeId='" + nodeId + '\'' +
                '}';
    }
}
