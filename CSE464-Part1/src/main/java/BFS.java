import java.util.ArrayList;
import java.util.List;

public class BFS extends GraphSearchTemplate {
    public BFS(Graph graph) {
        super(graph);
    }

    @Override
    protected void fetchNextNodesToSearch(Node node, List<Node> path) {
        for (String[] edge : graph.getEdges()) {
            if (edge[0].equals(node.getLabel())) {
                String neighbor = edge[1];
                if (!visited.contains(new Node(neighbor))) {
                    List<Node> newPath = new ArrayList<>(path);
                    newPath.add(new Node(neighbor));
                    queue.add(newPath);
                    visited.add(new Node(neighbor));
                }
            }
        }
    }
}
