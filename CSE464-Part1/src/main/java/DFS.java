import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DFS extends GraphSearchTemplate {
    public DFS(Graph graph) {
        super(graph);
    }

    @Override
    protected void fetchNextNodesToSearch(Node node, List<Node> path) {
        for (String[] edge : graph.getEdges()) {
            if (edge[0].equals(node.getLabel())) {
                Node neighbor = new Node(edge[1]);
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                    visited.add(neighbor);
                }
            }
        }
    }
}
