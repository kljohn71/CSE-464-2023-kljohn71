import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class BFS implements GraphSearchStrategy {
    private final Graph graph;

    public BFS(Graph graph) {
        this.graph = graph;
    }

    @Override
    public Path pathGraphSearch(Node src, Node dst) {
        List<Node> visited = new ArrayList<>();
        List<List<Node>> queue = new ArrayList<>();

        visited.add(src);
        queue.add(new ArrayList<>(Collections.singletonList(src)));

        while (!queue.isEmpty()) {
            List<Node> path = queue.remove(0);
            Node node = path.get(path.size() - 1);

            for (String[] edge : graph.getEdges()) {
                if (edge[0].equals(node.getLabel())) {
                    String neighbor = edge[1];
                    if (!visited.contains(new Node(neighbor))) {
                        List<Node> newPath = new ArrayList<>(path);
                        newPath.add(new Node(neighbor));
                        queue.add(newPath);
                        visited.add(new Node(neighbor));
                        if (neighbor.equals(dst.getLabel())) {
                            return new Path(newPath);
                        }
                    }
                }
            }
        }

        return null;
    }
}
