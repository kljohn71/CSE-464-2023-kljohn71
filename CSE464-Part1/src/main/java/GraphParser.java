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
            String nodesInfo = String.format("Number of Nodes: %d\n", nodes.size());
            output.append(nodesInfo);

            String edgesInfo = String.format("Number of Edges: %d\n", edges.size());
            output.append(edgesInfo);

            String nodesList = String.format("Nodes: %s\n", nodes);
            output.append(nodesList);

            output.append("Edges: [");

            for (String[] edge : edges) {
                String formattedEdge = String.format("[%s, %s], ", edge[0], edge[1]);
                output.append(formattedEdge);
            }

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
        GraphSearchAbstract graphSearch;

        if (algo == Algorithm.BFS) {
            graphSearch = new BFS(graph);
        } else if (algo == Algorithm.DFS) {
            graphSearch = new DFS(graph);
        } else {
            throw new IllegalArgumentException("Unsupported algorithm: " + algo);
        }

        return graphSearch.pathGraphSearch(src, dst);
    }
}
