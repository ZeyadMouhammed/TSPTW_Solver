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
        if (path1 == null) {
            return null;
        }

        String lastCityInPath1 = path1.getLast();
        List<String> path2 = solveTSPTW_DivideAndConquer(graph, subset2, lastCityInPath1);
        if (path2 == null) {
            return null;
        }

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
            allCities.addFirst(startCity);
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
                    optimalTime = cost[1];
                    optimalPath = perm;
                }
            }
        }
        return optimalPath;
    }

    private static List<String> mergePaths(Graph graph, List<String> path1, List<String> path2, String startCity) {

        List<String> mergedPath = new ArrayList<>(path1);
        Set<String> visited = new HashSet<>(path1);
        String lastCity = path1.getLast();

        for (String city : path2) {
            if (!visited.contains(city)) {
                int travelTime = graph.getEdgeTravelTime(lastCity, city);
                int arrivalTime = graph.getValidArrivalTime(lastCity, city, graph.calculateArrivalTime(mergedPath, travelTime));

                if (arrivalTime != -1) {
                    mergedPath.add(city);
                    visited.add(city);
                    lastCity = city;
                } else {
                    return null;
                }
            }
        }

        // Optionally return to the start city
        if (graph.isEdgeValid(lastCity, startCity)) {
            mergedPath.add(startCity);
        } else {
            return null;
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
