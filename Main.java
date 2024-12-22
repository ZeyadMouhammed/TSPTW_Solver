import javax.swing.*;
import java.util.List;

public class Main {
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
        graph.connectCities("B", "C", 4, 2);
        graph.connectCities("B", "D", 8, 5);
        graph.connectCities("C", "D", 6, 4);

        // Create and show the GUI
        SwingUtilities.invokeLater(() -> GraphVisualizer.createAndShowGUI(graph));

        // Solve TSPTW using brute force
        List<String> solution = BruteForce.solveTSPTW_BruteForce(graph, "A");

        int[] cost = {0, 0};

        // Print the solution
        if (solution == null) {
            System.out.println("No valid path found.");
        } else {
            System.out.println("Optimal Path: " + solution);
            cost = graph.calculatePathDistanceAndTime(solution);
            System.out.println("optimal path Distance: " + cost[0] + " Time: " + cost[1]);
        }


        System.out.println();

        solution = Greedy.solveTSPTW_Greedy(graph, "A");
        System.out.println("Greedy");
        // Print the solution
        if (solution == null) {
            System.out.println("No valid path found.");
        } else {
            System.out.println("Optimal Path: " + solution);
            cost = graph.calculatePathDistanceAndTime(solution);
            System.out.println("optimal path Distance: " + cost[0] + " Time: " + cost[1]);
        }


        System.out.println();

        solution = DivideAndConquer.solveTSPTW_DivideAndConquer(graph, graph.getAllCities(), "A");
        System.out.println("D & C");
        if (solution == null) {
            System.out.println("No valid path found.");
        } else {
            System.out.println("Optimal Path: " + solution);
            cost = graph.calculatePathDistanceAndTime(solution);
            System.out.println("optimal path Distance: " + cost[0] + " Time: " + cost[1]);
        }

        System.out.println();
        System.out.println("DP");
        System.out.println();
        // Example: Define cost matrix, travel time matrix, and time windows
        int[][] costMatrix = graph.toAdjacencyMatrix();

        int[][] travelTimeMatrix = graph.toTravelTimeMatrix();

        int[][] timeWindows = graph.toTimeWindowMatrix();

        int[] result = DynamicProgramming.SolveTSPTW_DP(costMatrix, travelTimeMatrix, timeWindows);
        if (result[0] != -1) {
            System.out.println("Minimum cost to solve TSPTW: " + result[0] + " Path Time: " +result[1]);
        } else {
            System.out.println("No feasible solution exists within the given time windows.");
        }

    }

}