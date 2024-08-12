package me.about.widget.jobflow.core.dag;

import lombok.Getter;
import lombok.Setter;
import me.about.widget.jobflow.enums.StageStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;


@Getter
public class Stage {

    @Setter
    private List<Node> nodes = new ArrayList<>();

    @Setter
    private StageStatus status;

    @Setter
    private long elapsed;

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
        long startedAt = System.currentTimeMillis();
        setStatus(StageStatus.RUNNING);
        if (!hasNode()) {
            setStatus(StageStatus.COMPLETED);
            return;
        }
        if (shouldConcurrent()) {
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    nodes.stream()
                            .map(node -> CompletableFuture
                                    .runAsync(node::executeNode, executor)
                            )
                            .toArray(CompletableFuture[]::new)
            );
            try {
                allFutures.join();
            } catch (CompletionException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
            }
        } else {
            nodes.forEach(Node::executeNode);
        }
        setStatus(StageStatus.COMPLETED);
        long elapsed = System.currentTimeMillis() - startedAt;
        setElapsed(elapsed);
    }
}
