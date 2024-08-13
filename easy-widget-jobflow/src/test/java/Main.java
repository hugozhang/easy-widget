import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class Graph {
    private int V; // 顶点的数量
    private List<List<Integer>> adj; // 邻接表

    Graph(int V) {
        this.V = V;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
    }

    void addEdge(int src, int dest) {
        adj.get(src).add(dest); // 添加边，src指向dest
    }

    public List<Integer> topologicalSort() {
        List<Integer> result = new ArrayList<>();
        int[] indegree = new int[V]; // 存储每个顶点的入度

        // 计算入度
        for (int i = 0; i < V; i++) {
            for (int j : adj.get(i)) {
                indegree[j]++;
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < V; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }

        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);

            for (int neighbor : adj.get(node)) {
                if (--indegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (result.size() != V) {
            throw new IllegalArgumentException("Graph has a cycle");
        }

        return result;
    }
}

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(5, 2);
        graph.addEdge(5, 0);
        graph.addEdge(4, 0);
        graph.addEdge(4, 1);
        graph.addEdge(2, 3);
        graph.addEdge(3, 1);

        try {
            List<Integer> topologicalOrder = graph.topologicalSort();
            System.out.println("Topological Sort Order: " + topologicalOrder);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
