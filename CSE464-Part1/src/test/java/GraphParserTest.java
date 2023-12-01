import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.*;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.parse.Parser;

public class GraphParserTest {
    GraphParser graphParser;
    private String testDotFilePath = "test_graph.dot";

    @Before
    public void setUp() {
        graphParser = new GraphParser();
    }

    @After
    public void tearDown() {
        graphParser = null;
        File dotFile = new File("output_graph.dot");
        if (dotFile.exists()) {
            File newDotFile = new File("test/output_graph.dot");
            dotFile.renameTo(newDotFile);
            System.out.println("Output DOT file path: " + newDotFile.getAbsolutePath());
        }

        File pngFile = new File("output_graph.png");
        if (pngFile.exists()) {
            File newPngFile = new File("test/output_graph.png");
            pngFile.renameTo(newPngFile);
            System.out.println("Output PNG file path: " + newPngFile.getAbsolutePath());
        }
    }

    @Test
    public void testParseGraph() {
        graphParser.parseGraph(testDotFilePath);
        assertEquals(3, graphParser.getGraph().getNodes().size());
        assertEquals(3, graphParser.getGraph().getEdges().size());
    }

    @Test
    public void testAddNode() {
        graphParser.parseGraph(testDotFilePath);
        GraphParser.Node nodeA = graphParser.new Node("a");

        graphParser.addNode(nodeA);

        assertEquals(4, graphParser.getGraph().getNodes().size());
    }

    @Test
    public void testAddNodes() {
        GraphParser graphParser = new GraphParser();
        GraphParser.Node nodeA = graphParser.new Node("a");
        GraphParser.Node nodeB = graphParser.new Node("b");

        graphParser.addNodes(new GraphParser.Node[]{nodeA, nodeB});

        assertEquals(2, graphParser.getGraph().getNodes().size());
    }

    @Test
    public void testAddEdge() {
        GraphParser graphParser = new GraphParser();
        GraphParser.Node nodeA = graphParser.new Node("a");
        GraphParserNode nodeB = graphParser.new Node("b");

        graphParser.addNode(nodeA);
        graphParser.addNode(nodeB);
        graphParser.addEdge(nodeA, nodeB);

        assertEquals(1, graphParser.getGraph().getEdges().size());
    }

    @Test
    public void testOutputDOTGraph() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.outputDOTGraph("output_graph.dot");

        File outputDotFile = new File("output_graph.dot");
        assertTrue(outputDotFile.exists());
        try (BufferedReader generatedOutput = new BufferedReader(new FileReader("output_graph.dot"));
             BufferedReader originalTestDot = new BufferedReader(new FileReader("original_test_graph.dot"))) {

            String generatedLine;
            String originalLine;

            while ((generatedLine = generatedOutput.readLine()) != null) {
                originalLine = originalTestDot.readLine();
                assertEquals(originalLine, generatedLine);
            }

            assertNull(originalTestDot.readLine());

            List<GraphParser.Node> edges = graphParser.getGraph().getEdges();

            for (int i = 0; i < edges.size(); i += 2) {
                GraphParser.Node sourceNode = edges.get(i);
                GraphParser.Node destinationNode = edges.get(i + 1);
                String expectedEdge = "\t" + sourceNode.getLabel() + " -> " + destinationNode.getLabel() + ";";
                assertEquals(expectedEdge, generatedOutput.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOutputGraphics() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.outputGraphics("output_graph", "png");

        File outputPngFile = new File("output_graph.png");
        assertTrue(outputPngFile.exists());
    }

    @Test
    public void testOutputGraphWithExpectedFile() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.outputGraph("output_graph.txt");
        try (BufferedReader generatedOutput = new BufferedReader(new FileReader("output_graph.txt"));
             BufferedReader expectedOutput = new BufferedReader(new FileReader("expected_output.txt"))) {

            String generatedLine;
            String expectedLine;

            while ((generatedLine = generatedOutput.readLine()) != null) {
                expectedLine = expectedOutput.readLine();
                assertEquals(expectedLine, generatedLine);
            }

            assertNull(expectedOutput.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRemoveNode() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeNode("A");

        assertEquals(2, graphParser.getGraph().getNodes().size());
        assertEquals(1, graphParser.getGraph().getEdges().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentNode() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeNode("X");
    }

    @Test
    public void testRemoveNodes() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeNodes(new String[]{"A", "B"});

        assertEquals(1, graphParser.getGraph().getNodes().size());
        assertEquals(0, graphParser.getGraph().getEdges().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentNodes() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeNodes(new String[]{"X", "Y"}); 
    }

    @Test
    public void testRemoveEdge() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeEdge("A", "B");

        assertEquals(3, graphParser.getGraph().getNodes().size());
        assertEquals(2, graphParser.getGraph().getEdges().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentEdge() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeEdge("A", "X");
    }
}
