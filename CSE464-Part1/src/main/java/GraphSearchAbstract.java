import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class GraphSearchAbstract {
    protected Graph graph;
    protected List<Node> visited;
    protected List<List<Node>> queue;

    public GraphSearchAbstract(Graph graph) {
        this.graph = graph;
        this.visited = new ArrayList<>();
        this.queue = new ArrayList<>();
    }

    public Path pathGraphSearch(Node src, Node dst) {
        visited.add(src);
        queue.add(new ArrayList<>(Collections.singletonList(src)));

        while (!queue.isEmpty()) {
            List<Node> path = queue.remove(0);
            Node node = path.get(path.size() - 1);

            fetchNextNodesToSearch(node, path);

            if (node.equals(dst)) {
                return new Path(new ArrayList<>(path));
            }
        }

        return null;
    }

    protected abstract void fetchNextNodesToSearch(Node node, List<Node> path);
}

