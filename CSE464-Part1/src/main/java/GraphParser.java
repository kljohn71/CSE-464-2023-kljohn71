import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        private List<Node> nodes;
        private List<Node> edges;

        public Graph() {
            nodes = new ArrayList<>();
            edges = new ArrayList<>();
        }

        public void addNode(Node node) {
            if (!nodes.contains(node)) {
                nodes.add(node);
            }
        }

        public void addNodes(Node[] nodeArray) {
            Collections.addAll(nodes, nodeArray);
        }

        public void addEdge(Node sourceNode, Node destinationNode) {
            edges.add(sourceNode);
            edges.add(destinationNode);
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public List<Node> getEdges() {
            return edges;
        }

        @Override
        public String toString() {
            StringBuilder output = new StringBuilder();
            output.append("Number of Nodes: ").append(nodes.size()).append("\n");
            output.append("Number of Edges: ").append(edges.size()).append("\n");
            output.append("Nodes: ").append(nodes).append("\n");

            output.append("Edges: [");

            // Format each edge array
            for (int i = 0; i < edges.size(); i += 2) {
                output.append("[")
                    .append(edges.get(i)).append(" -> ").append(edges.get(i + 1))
                    .append("], ");
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
                    String[] parts = line.split("->");
                String sourceNodeLabel = parts[0].trim();
                String destinationNodeLabel = parts[1].trim().split(";")[0].trim();
                Node sourceNode = new Node(sourceNodeLabel);
                Node destinationNode = new Node(destinationNodeLabel);
                graph.addNode(sourceNode);
                graph.addNode(destinationNode);
                graph.addEdge(sourceNode, destinationNode);
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

    public void outputGraphics(String filePath, String format) {
        if (!format.equals("png")) {
            System.out.println("Unsupported format. Only 'png' is supported.");
            return;
        }

        String dotFilePath = filePath + ".dot";
        outputDOTGraph(dotFilePath);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("dot", "-T" + format, dotFilePath, "-o", filePath);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        new File(dotFilePath).delete();
    }

    private String toDotString() {
        StringBuilder dotString = new StringBuilder();
        dotString.append("digraph G {\n");
        for (Node node : graph.edges) {
            dotString.append("\t").append(node).append("\n");
        }
        dotString.append("}\n");
        return dotString.toString();
    }

    public void removeNode(String label) {
    final Node[] nodeToRemove = {null};

    for (Node graphNode : graph.getNodes()) {
        if (graphNode.getLabel().equals(label)) {
            nodeToRemove[0] = graphNode;
            break;
        }
    }

    if (nodeToRemove[0] != null) {
        graph.getNodes().remove(nodeToRemove[0]); // Remove the node

        // Remove connected edges
        graph.getEdges().removeIf(edge -> edge.getLabel().equals(nodeToRemove[0].getLabel()) || edge.getLabel().equals(nodeToRemove[0].getLabel())
        );
    }
}

    public void removeNodes(String[] labels) {
        for (String label : labels) {
            removeNode(label);
        }
    }

    public void removeEdge(String srcLabel, String dstLabel) {
        graph.getEdges().removeIf(edge -> edge.getLabel().equals(srcLabel) && edge.getLabel().equals(dstLabel));
    }

    public enum Algorithm {
        BFS,
        DFS
    }

    public Path pathGraphSearch(Node src, Node dst, Algorithm algo) {
        StringBuilder dotString = new StringBuilder();
        if (algo == Algorithm.BFS) {
            List<Node> visited = new ArrayList<>();
            List<List<Node>> queue = new ArrayList<>();

            visited.add(src);
            queue.add(new ArrayList<>(Collections.singletonList(src)));

            while (!queue.isEmpty()) {
                List<Node> path = queue.remove(0);
                Node node = path.get(path.size() - 1);

                for (Node var : graph.getEdges()) {
                    dotString.append("\t").append(var).append(" -> ").append(var).append(";\n");
                    if (var.equals(node.getLabel())) {
                        Node neighbor = var;
                        if (!visited.contains(neighbor)) {
                            List<Node> newPath = new ArrayList<>(path);
                            newPath.add(neighbor);
                            queue.add(newPath);
                            visited.add(neighbor);
                            if (neighbor.equals(dst.getLabel())) {
                                return new Path(newPath);
                            }
                        }
                    }
                }
            }

            return null;
        } else if (algo == Algorithm.DFS) {
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

                for (Node node : graph.getEdges()) {
                    if (node.equals(current.getLabel())) {
                        Node neighbor = node;
                        if (!visited.contains(neighbor)) {
                            stack.push(neighbor);
                            visited.add(neighbor);
                        }
                    }
                }
            }

            return null;
        }
        return null;
    }
}
