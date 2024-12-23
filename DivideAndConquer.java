import javax.swing.*;
import java.util.*;

public class DivideAndConquer {

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

        // Solve TSPTW using Divide and Conquer approach
        List<String> solution = solveTSPTW_DivideAndConquer(graph, graph.getAllCities(), "A");

        System.out.println();

        // Print the solution
        if (solution == null) {
            System.out.println("No valid path found.");
        } else {
            System.out.println("Optimal Path: " + solution);
            int[] cost = graph.calculateFeasiblePathCost(solution);
            System.out.println("Optimal path distance: " + cost[0] + " Time: " + cost[1]);
        }
    }

    /**
     * Solve TSPTW using Divide and Conquer approach.
     *
     * @param graph     The graph representing the TSPTW problem.
     * @param cities    The list of cities to visit.
     * @param startCity The starting city.
     * @return The optimal path as a list of city names.
     */
    public static List<String> solveTSPTW_DivideAndConquer(Graph graph, List<String> cities, String startCity) {
        // Base case: If only a small number of cities, solve directly
        if (cities.size() <= 3) {
            return solveSmallTSP(graph, cities, startCity);
        }

        // Divide the cities into two subsets
        int mid = cities.size() / 2;
        List<String> subset1 = cities.subList(0, mid);
        List<String> subset2 = cities.subList(mid, cities.size());

        // Recursively solve TSPTW for each subset
        List<String> path1 = solveTSPTW_DivideAndConquer(graph, subset1, startCity);
        List<String> path2 = solveTSPTW_DivideAndConquer(graph, subset2, path1.get(path1.size() - 1));

        // Combine the two paths
        return mergePaths(graph, path1, path2, startCity);
    }

    /**
     * Solve TSP for a small number of cities using brute force.
     */
    private static List<String> solveSmallTSP(Graph graph, List<String> cities, String startCity) {
        // Add the start city to the list for permutations
        List<String> allCities = new ArrayList<>(cities);
        if (!allCities.contains(startCity)) {
            allCities.add(0, startCity);
        }

        // Generate all permutations
        List<List<String>> permutations = generatePermutations(allCities);

        // Find the optimal path
        List<String> optimalPath = null;
        int optimalCost = Integer.MAX_VALUE;
        int optimalTime = Integer.MAX_VALUE;

        for (List<String> perm : permutations) {
            int[] cost = graph.calculateFeasiblePathCost(perm);
            if (cost != null) {
                // Check if the current path has a lower distance, or same distance with lower time
                if (cost[0] < optimalCost || (cost[0] == optimalCost && cost[1] < optimalTime)) {
                    optimalCost = cost[0];
                    optimalTime = cost[1];  // Store the time for comparison in future iterations
                    optimalPath = perm;
                }
            }
        }

        return optimalPath;
    }

    private static List<String> mergePaths(Graph graph, List<String> path1, List<String> path2, String startCity) {
        List<String> mergedPath = new ArrayList<>(path1);

        // Track visited cities to avoid duplication
        Set<String> visited = new HashSet<>(path1);

        // Add the best transition city and remaining path
        String lastCity = path1.get(path1.size() - 1);

        // Merge path2 into path1
        for (String city : path2) {
            if (!visited.contains(city)) {
                // Check if the edge is valid and feasible
                int travelTime = graph.getEdgeTravelTime(lastCity, city);
                int arrivalTime = graph.calculateArrivalTime(mergedPath, travelTime);

                // Calculate feasible path cost and check if it's valid
                int pathCost = graph.calculateFeasiblePathCost(mergedPath)[0];
                if (pathCost == -1) {
                    return null;  // Return null if the path is not feasible
                }

                if (graph.isEdgeValid(lastCity, city) && graph.isEdgeWithinLatestTimeWindow(lastCity, city, arrivalTime)) {
                    mergedPath.add(city);
                    visited.add(city);
                    lastCity = city; // Update lastCity for subsequent checks
                } else {
                    return null;  // Return null if the path can't be completed due to invalid city
                }
            }
        }

        // Optionally, return to the start city
        if (graph.isEdgeValid(lastCity, startCity) && visited.contains(startCity)) {
            mergedPath.add(startCity);
        } else {
            return null;  // Return null if unable to return to the start city
        }

        return mergedPath;
    }

    /**
     * Generate all permutations of a list of cities.
     */
    private static List<List<String>> generatePermutations(List<String> cities) {
        List<List<String>> result = new ArrayList<>();
        permute(cities, 0, result);
        return result;
    }

    private static void permute(List<String> cities, int start, List<List<String>> result) {
        if (start == cities.size()) {
            result.add(new ArrayList<>(cities));
            return;
        }

        for (int i = start; i < cities.size(); i++) {
            Collections.swap(cities, start, i);
            permute(cities, start + 1, result);
            Collections.swap(cities, start, i);
        }
    }

    private static int calculateArrivalTime(Graph graph, List<String> path, int travelTime) {
        int currentTime = 0;

        // Calculate cumulative travel time for the current path
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);

            int edgeTravelTime = graph.getEdgeTravelTime(from, to);
            if (edgeTravelTime == -1) {
                // Invalid edge, terminate early (or handle it accordingly)
                return -1;
            }
            currentTime += edgeTravelTime;
        }

        // Add the travel time for the next edge
        return currentTime + travelTime;
    }
}
