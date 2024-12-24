import javax.swing.*;

public class TestCase3 {

    public static void main(String[] args) {
        // Create a graph object
        Graph graph = new Graph();

        // Add cities with time windows
        graph.addCity("A", 0, 20);
        graph.addCity("B", 2, 15);
        graph.addCity("C", 5, 20);
        graph.addCity("D", 10, 25);

        // Connect cities with distances and travel times
        graph.connectCities("A", "B", 5, 3);
        graph.connectCities("A", "C", 10, 6);
        graph.connectCities("A", "D", 5, 3);
        graph.connectCities("B", "C", 4, 2);
        graph.connectCities("B", "D", 8, 5);
        graph.connectCities("C", "D", 6, 4);

        // Create and show the GUI
        SwingUtilities.invokeLater(() -> GraphVisualizer.createAndShowGUI(graph, "A"));
    }

}
