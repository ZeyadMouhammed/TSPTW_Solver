import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BruteForce {

    /**
     * Solves the TSPTW problem using a brute-force approach.
     *
     * @param graph The graph object containing cities and connections.
     * @param start The starting city name.
     * @return The optimal path as a list of city names, or null if no valid path exists.
     */
    public static List<String> solveTSPTW_BruteForce(Graph graph, String start) {
        // Get all cities in the graph
        List<String> cities = graph.getAllCities();

        // Remove the start city from the list for permutations
        cities.remove(start);

        // Generate all permutations of the remaining cities
        List<List<String>> permutations = generatePermutations(cities);

        // Variables to store the optimal path, cost, and time
        List<String> optimalPath = null;
        int optimalCost = Integer.MAX_VALUE;
        int optimalTime = Integer.MAX_VALUE;

        // Evaluate each permutation
        for (List<String> perm : permutations) {
            // Add the start city to the beginning and end of the path
            List<String> path = new ArrayList<>();
            path.add(start);
            path.addAll(perm);
            path.add(start);

            // Calculate the cost and time of the path
            int[] result = graph.calculateFeasiblePathCost(path);
            if (result == null) {
                continue; // Skip invalid paths
            }
            int cost = result[0]; // Total distance
            int time = result[1]; // Total time

            // Update the optimal path if it's better
            if (cost < optimalCost || (cost == optimalCost && time < optimalTime)) {
                optimalCost = cost;
                optimalTime = time;
                optimalPath = path;
            }
        }

        // Return the optimal path, or null if no valid path exists
        return optimalPath;
    }


    /**
     * Generates all permutations of a list of cities.
     *
     * @param cities The list of city names.
     * @return A list of all permutations.
     */
    private static List<List<String>> generatePermutations(List<String> cities) {
        List<List<String>> result = new ArrayList<>();
        permute(cities, 0, result);
        return result;
    }

    /**
     * Helper method to generate permutations recursively.
     *
     * @param cities The list of city names.
     * @param start  The starting index for permutation.
     * @param result The list to store all permutations.
     */
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
