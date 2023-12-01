import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DFS implements GraphSearchStrategy {
    private final Graph graph;

    public DFS(Graph graph) {
        this.graph = graph;
    }

    @Override
    public Path pathGraphSearch(Node src, Node dst) {
        List<Node> visited = new ArrayList<>();
        List<Node> path = new ArrayList<>();

        Stack<Node> stack = new Stack<>();
        stack.push(src);
        visited.add(src);

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            path.add(current);

            if (current.equals(dst)) {
                return new Path(new ArrayList<>(path));
            }

            for (String[] edge : graph.getEdges()) {
                if (edge[0].equals(current.getLabel())) {
                    Node neighbor = new Node(edge[1]);
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                        visited.add(neighbor);
                    }
                }
            }
        }

        return null;
    }
}
