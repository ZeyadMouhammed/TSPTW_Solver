import java.util.*;

public class DivideAndConquer {

    public static List<String> solveTSPTW_DivideAndConquer(Graph graph, List<String> cities, String startCity) {
        // Base case: Solve directly if small number of cities
        if (cities.size() <= 3) {
            return solveSmallTSP(graph, cities, startCity);
        }

        // Divide the cities into two subsets
        int mid = cities.size() / 2;
        List<String> subset1 = new ArrayList<>(new HashSet<>(cities.subList(0, mid)));
        List<String> subset2 = new ArrayList<>(new HashSet<>(cities.subList(mid, cities.size())));

        // Ensure the starting city is included in the first subset
        if (!subset1.contains(startCity)) {
            subset1.addFirst(startCity);
        }

        // Recursively solve for each subset
        List<String> path1 = solveTSPTW_DivideAndConquer(graph, subset1, startCity);
        if (path1 == null) return null;

        String lastCityInPath1 = path1.getLast();
        List<String> path2 = solveTSPTW_DivideAndConquer(graph, subset2, lastCityInPath1);
        if (path2 == null) return null;

        // Merge paths and enforce returning to startCity
        return mergePaths(graph, path1, path2, startCity);
    }

    private static List<String> solveSmallTSP(Graph graph, List<String> cities, String startCity) {
        List<String> allCities = new ArrayList<>(cities);
        if (!allCities.contains(startCity)) {
            allCities.addFirst(startCity);
        }

        List<List<String>> permutations = generatePermutations(allCities);
        List<String> optimalPath = null;
        int optimalCost = Integer.MAX_VALUE;

        for (List<String> perm : permutations) {
            if (!perm.getFirst().equals(startCity)) continue; // Ensure path starts with startCity

            if (!perm.getLast().equals(startCity) && graph.isEdgeValid(perm.getLast(), startCity)) {
                perm.add(startCity); // Ensure returning to startCity
            }

            if (new HashSet<>(perm).size() < perm.size()) continue; // Skip permutations with duplicates

            int[] cost = graph.calculateFeasiblePathCost(perm);
            if (cost != null && cost[0] < optimalCost) {
                optimalCost = cost[0];
                optimalPath = new ArrayList<>(perm);
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
                if (arrivalTime == -1) return null;

                mergedPath.add(city);
                visited.add(city);
                lastCity = city;
            }
        }

        // Ensure returning to the startCity
        if (graph.isEdgeValid(lastCity, startCity)) {
            mergedPath.add(startCity);
        } else {
            return null;
        }
        return mergedPath;
    }

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
