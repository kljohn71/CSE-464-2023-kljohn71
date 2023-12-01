import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Stack;

public class GraphParser {
    public class Node {
        private String label;

        public Node(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public class Path {
        private List<Node> nodes;

        public Path(List<Node> nodes) {
            this.nodes = nodes;
        }

        public List<Node> getNodes() {
            return nodes;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < nodes.size() - 1; i++) {
                result.append(nodes.get(i)).append(" -> ");
            }
            result.append(nodes.get(nodes.size() - 1));
            return result.toString();
        }
    }

    public class Graph {
        private List<String> nodes;
        private List<String[]> edges;

        public Graph() {
            nodes = new ArrayList<>();
            edges = new ArrayList<>();
        }

        public void addNode(String node) {
            if (!nodes.contains(node)) {
                nodes.add(node);
            }
        }

        public void addNodes(String[] labels) {
            for (String label : labels) {
                addNode(label);
            }
        }

        public void addEdge(String sourceNode, String destinationNode) {
            edges.add(new String[]{sourceNode, destinationNode});
        }

        public List<String> getNodes() {
            return nodes;
        }

        public List<String[]> getEdges() {
            return edges;
        }

        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            output.append("Number of Nodes: ").append(nodes.size()).append("\n");
            output.append("Number of Edges: ").append(edges.size()).append("\n");
            output.append("Nodes: ").append(nodes).append("\n");
            output.append("Edges: [");
            // Format each edge
            for (String[] edge : edges) {
                output.append("[").append(edge[0]).append(", ").append(edge[1]).append("], ");
            }
            // Remove the trailing comma and space if there are edges
            if (!edges.isEmpty()) {
                output.setLength(output.length() - 2);
            }
            output.append("]\n");
                return output.toString();
        }
    }
    private final Graph graph;

    public GraphParser() {
        graph = new Graph();
    }
    private void processGraphLine(String line) {
        String[] edgeParts = line.split("->");
        String sourceNode = edgeParts[0].trim();
        String destinationNode = edgeParts[1].trim().split(";")[0].trim();
        graph.addNode(sourceNode);
        graph.addNode(destinationNode);
        graph.addEdge(sourceNode, destinationNode);
    }

    public void parseGraph(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isDigraph = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("digraph")) {
                    isDigraph = true;
                } else if (isDigraph && line.startsWith("}")) {
                    isDigraph = false;
                } else if (isDigraph && line.contains("->")) {
                    processGraphLine(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Graph getGraph() {
        return graph;
    }

    public void outputGraph(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(graph.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputDOTGraph(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(toDotString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toDotString() {
        StringBuilder dotString = new StringBuilder();
        String graphStart = "digraph G {\n";
        String graphEnd = "}\n";
        dotString.append(graphStart);
        for (String[] edge : graph.getEdges()) {
            String edgeString = "\t" + edge[0] + " -> " + edge[1] + ";\n";
            dotString.append(edgeString);
        }
        dotString.append(graphEnd);
        return dotString.toString();
    }

    public void removeNode(String label){
        if (!graph.getNodes().contains(label)) {
            throw new IllegalArgumentException("Node does not exist: " + label);
        }
        graph.getNodes().remove(label);
        graph.getEdges().removeIf(edge -> edge[0].equals(label) || edge[1].equals(label));
    }

    public void removeNodes(String[] labels) {
        for (String label : labels) {
            if (!graph.getNodes().contains(label)) {
                throw new IllegalArgumentException("Node does not exist: " + label);
            }
        }
        for (String label : labels) {
            removeNode(label);
        }
    }

    public void removeEdge(String srcLabel, String dstLabel) {
        if (!graph.getEdges().stream().anyMatch(edge -> edge[0].equals(srcLabel) && edge[1].equals(dstLabel))) {
            throw new IllegalArgumentException("Edge does not exist: " + srcLabel + " -> " + dstLabel);
        }
        graph.getEdges().removeIf(edge -> edge[0].equals(srcLabel) && edge[1].equals(dstLabel));
    }

    public enum Algorithm {
        BFS,
        DFS
    }

    public Path pathGraphSearch(Node src, Node dst, Algorithm algo) {
        if (algo == Algorithm.BFS) {
            return performBFS(src, dst);
        } 
        else if (algo == Algorithm.DFS) {
            return performDFS(src, dst);
        }
        return null;
    }

    private Path performBFS(Node src, Node dst) {
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

    private Path performDFS(Node src, Node dst) {
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
