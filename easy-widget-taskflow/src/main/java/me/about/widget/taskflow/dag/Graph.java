package me.about.widget.taskflow.dag;

import com.google.common.base.Joiner;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;


public class Graph {

    int v; // 顶点的数量
    List<List<Integer>> adj; // 邻接表
    Map<Node, Integer> nodeIndexMap = new HashMap<>();
    Map<Integer, Node> indexNodeMap = new HashMap<>();
    int nextIndex = 0;


    @Getter
    @Setter
    private List<Stage> stages = new ArrayList<>();

    private void addStage(Stage stage) {
        stages.add(stage);
    }

    public Graph(int v) {
        this.v = v;
        adj = new ArrayList<>(v);
        for (int i = 0; i < v; i++) {
            adj.add(new ArrayList<>());
        }
    }

    private void addVertex(Node node) {
        if(node != null) {
            nodeIndexMap.computeIfAbsent(node, k -> {
                indexNodeMap.put(nextIndex, node);
                return nextIndex++;
            });
        }
    }

    public void addEdge(Node src, Node dest) {
        if(src == null) {
            throw new IllegalArgumentException("src is null");
        }
        // 为src和dest分配索引，如果它们还没有索引的话
        addVertex(src);
        addVertex(dest);

        // 使用映射中存储的索引来添加边
        if(dest != null) {
            int srcIdx = nodeIndexMap.get(src);
            int destIdx = nodeIndexMap.get(dest);
            adj.get(srcIdx).add(destIdx);
        }
    }


    public void topologicalSort() {
        List<List<Integer>> result = new ArrayList<>();
        int[] indegree = new int[v]; // 存储每个顶点的入度

        // 计算入度
        for (int i = 0; i < v; i++) {
            for (int j : adj.get(i)) {
                indegree[j]++;
            }
        }

        //dfs 准备
        Queue<Integer> queue = new LinkedList<>();
        //分组任务
        List<Integer> group = new ArrayList<>();

        for (int i = 0; i < v; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
                group.add(i);
            }
        }
        result.add(group);

        //dfs
        while (!queue.isEmpty()) {
            int node = queue.poll();
            List<Integer> dep = adj.get(node);
            if (!dep.isEmpty()) {
                List<Integer> group2 = new ArrayList<>();
                for (int neighbor : dep) {
                    if (--indegree[neighbor] == 0) {
                        queue.add(neighbor);
                        group2.add(neighbor);
                    }
                }
                result.add(group2);
            }
        }

        int totalElements = 0;
        for(List<Integer> sublist: result) {
            totalElements += sublist.size();
            Stage stage = new Stage();
            for(Integer i: sublist){
                stage.addNode(indexNodeMap.get(i));
            }
            if(stage.hasNode()){
                addStage(stage);
            }
        }

        if (totalElements != v) {
            throw new IllegalArgumentException("Graph has a cycle");
        }
    }

    @Override
    public String toString() {
        List<String> print = stages.stream().map(stage -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Stage{status:")
                    .append(stage.getStatus())
                    .append(", nodes: [");
            stage.getNodes().forEach(node -> {
                sb.append("{id:")
                        .append(node.getNodeId())
                        .append(", status:")
                        .append(node.getStatus())
                        .append("}, ");
            });
            return sb.append("]},").toString();
        }).collect(Collectors.toList());
        return "Graph : " + Joiner.on(",").join(print);
    }

}
