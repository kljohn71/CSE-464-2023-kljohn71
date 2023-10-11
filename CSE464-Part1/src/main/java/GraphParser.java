import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphParser {

    public class Graph {
        private List<String> nodes;
        private List<String[]> edges;

        public Graph() {
            nodes = new ArrayList<>();
            edges = new ArrayList<>();
        }

        public void addNode(String node) {
            nodes.add(node);
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
            return "Nodes: " + nodes + "\nEdges: " + edges;
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
                    String sourceNode = parts[0].trim();
                    String destinationNode = parts[1].trim().split(";")[0].trim(); // Remove the semicolon
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
}
