package me.about.widget.taskflow.dag;

import lombok.Getter;
import lombok.Setter;
import me.about.widget.taskflow.enums.NodeStatus;
import me.about.widget.taskflow.enums.StageStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


@Getter
public class Stage {

    private final List<Node> nodes = new ArrayList<>();

    @Setter
    private StageStatus status;

    public Stage() {
        setStatus(StageStatus.PENDING);
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public boolean shouldConcurrent() {
        return nodes.size() >= 2;
    }

    public boolean hasNode() {
        return !nodes.isEmpty();
    }

    public void executeStage(Executor executor) {
        setStatus(StageStatus.RUNNING);
        if (!hasNode()) {
            setStatus(StageStatus.COMPLETED);
            return;
        }
        if (shouldConcurrent()) {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    nodes.stream()
                            .map(node -> CompletableFuture.runAsync(() -> {
                                executeNode(node);
                            }, executor))
                            .toArray(CompletableFuture[]::new)
            );
            allFutures.join();
        } else {
            nodes.forEach(Stage::executeNode);
        }
        setStatus(StageStatus.COMPLETED);
    }

    private static void executeNode(Node node) {
        node.setStatus(NodeStatus.RUNNING);
        node.getTask().execute();
        node.setStatus(NodeStatus.COMPLETED);
    }
}
