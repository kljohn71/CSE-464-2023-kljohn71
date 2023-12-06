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
    private GraphParser graphParser;
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
        assertEquals(3, graphParser.graph.getNodes().size());
        assertEquals(3, graphParser.graph.getEdges().size());
    }

    @Test
    public void testAddNode() {
        graphParser.graph.addNode("A");
        graphParser.graph.addNode("B");
        assertEquals(2, graphParser.graph.getNodes().size());
    }

    @Test
    public void testAddNodes() {
        String[] labels = {"A", "B", "C"};
        graphParser.graph.addNodes(labels);
        assertEquals(3, graphParser.graph.getNodes().size());
    }

    @Test
    public void testAddEdge() {
        graphParser.graph.addEdge("A", "B");
        assertEquals(1, graphParser.graph.getEdges().size());
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
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        graphParser.removeNode("a");

        assertEquals(2, graphParser.graph.getNodes().size());
        assertEquals(1, graphParser.graph.getEdges().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentNode() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeNode("X");
    }

    @Test
    public void testRemoveNodes() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeNodes(new String[]{"a", "b"});

        assertEquals(1, graphParser.graph.getNodes().size());
        assertEquals(0, graphParser.graph.getEdges().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentNodes() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeNodes(new String[]{"X", "Y"}); 
    }

    @Test
    public void testRemoveEdge() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeEdge("a", "b");

        assertEquals(3, graphParser.graph.getNodes().size());
        assertEquals(2, graphParser.graph.getEdges().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentEdge() {
        graphParser.parseGraph(testDotFilePath);
        graphParser.removeEdge("A", "X");
    }
}
