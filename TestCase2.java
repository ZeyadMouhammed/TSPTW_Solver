import javax.swing.*;

public class TestCase2 {
    public static void main(String[] args) {
        // Create a graph object
        Graph graph = new Graph();

        // Add cities with time windows
        graph.addCity("A", 0, 20);
        graph.addCity("B", 5, 10);
        graph.addCity("C", 12, 20);

        // Connect cities with distances and travel times
        graph.connectCities("A", "B", 1, 1);
        graph.connectCities("B", "C", 1, 1);
        graph.connectCities("C", "A", 1, 1);

        // Create and show the GUI
        SwingUtilities.invokeLater(() -> GraphVisualizer.createAndShowGUI(graph, "A"));
    }
}
