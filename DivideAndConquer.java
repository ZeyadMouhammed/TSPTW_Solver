import java.util.*;

public class DivideAndConquer {

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
        List<String> path2 = solveTSPTW_DivideAndConquer(graph, subset2, path1.getLast());

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
            allCities.add(0, startCity); // Add the start city to the beginning
        }

        // Generate all permutations of the cities (excluding the start city)
        List<List<String>> permutations = generatePermutations(allCities);

        // Variables to store the optimal path, cost, and time
        List<String> optimalPath = null;
        int optimalCost = Integer.MAX_VALUE;
        int optimalTime = Integer.MAX_VALUE;

        // Evaluate each permutation
        for (List<String> perm : permutations) {
            // Add the start city to the beginning and end of the path
            List<String> path = new ArrayList<>();
            path.add(startCity);
            path.addAll(perm);
            path.add(startCity);

            // Calculate the cost and time of the path
            int[] result = graph.calculateFeasiblePathCost(path);
            if (result == null) {
                continue; // Skip invalid paths
            }
            int cost = result[0]; // Total distance
            int time = result[1]; // Total time

            // Debugging output for the current path, cost, and time
            System.out.println("Checking path: " + path);
            System.out.println("Cost (Distance): " + cost);
            System.out.println("Time: " + time);

            // Update the optimal path if it's better
            if (cost < optimalCost || (cost == optimalCost && time < optimalTime)) {
                optimalCost = cost;
                optimalTime = time;
                optimalPath = path;
                System.out.println("New optimal path found: " + optimalPath);
                System.out.println("New optimal cost: " + optimalCost);
                System.out.println("New optimal time: " + optimalTime);
            }
        }

        return optimalPath;
    }



    /**
     * Merge two paths into a single path while respecting time windows.
     */
    private static List<String> mergePaths(Graph graph, List<String> path1, List<String> path2, String startCity) {

        List<String> mergedPath = new ArrayList<>(path1);

        // Track visited cities to avoid duplication
        Set<String> visited = new HashSet<>(path1);

        // Add the best transition city and remaining path
        String lastCity = path1.getLast();

        // Merge path2 into path1
        for (String city : path2) {
            if (!visited.contains(city)) {
                // Check if the edge is valid and feasible
                int travelTime = graph.getEdgeTravelTime(lastCity, city);
                int arrivalTime = graph.calculateArrivalTime(mergedPath, travelTime);

                // Calculate feasible path cost and check if it's valid
                int pathCost = graph.calculateFeasiblePathTime(mergedPath);
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

        // Check if all cities have been visited and the path is complete
        for (String city : path2) {
            if (!visited.contains(city)) {
                return null;  // Return null if a required city is missing
            }
        }

        // Optionally, return to the start city
        if (graph.isEdgeValid(lastCity, startCity) && visited.contains(startCity)) {
            mergedPath.add(startCity);
        } else {
            return null;  // Return null if we can't return to the start city
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

}
