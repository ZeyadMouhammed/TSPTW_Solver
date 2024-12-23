import java.util.*;

public class Greedy {

    /**
     * Solve TSPTW using Greedy algorithm
     *
     * @param graph     The graph representing the TSPTW problem
     * @param startCity The starting node
     * @return A list of node names representing the path
     */
    public static List<String> solveTSPTW_Greedy(Graph graph, String startCity) {
        List<String> path = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        String currentCity = startCity;
        int currentTime = 0;

        // Add the start node to the path and mark it visited
        path.add(currentCity);
        visited.add(currentCity);

        // While there are unvisited nodes
        while (visited.size() < graph.getNumberOfCities()) {
            String nextNode = null;
            int minDistance = Integer.MAX_VALUE;

            // Check all cities that are not visited
            for (String city : graph.getAllCities()) {
                if (!visited.contains(city)) {
                    // Check if an edge exists between current city and next city
                    if (graph.isEdgeValid(currentCity, city)) {
                        // Calculate path distance (replace travel time with distance)
                        int distance = graph.getEdgeTravelDistance(currentCity, city);

                        // Check if the city is feasible to visit (based on distance)
                        if (graph.isEdgeWithinLatestTimeWindow(currentCity, city, distance)) {
                            // Select the city with the smallest distance
                            if (distance < minDistance) {
                                minDistance = distance;
                                nextNode = city;
                            }
                        }
                    }
                }
            }

            if (nextNode == null) {
                // If no valid next city is found, return null
                return null;
            }

            // Update path, visited cities, and current time
            visited.add(nextNode);
            path.add(nextNode);
            currentCity = nextNode;
            currentTime += minDistance; // Update the current distance after visiting the city
        }

        // Optionally return to the start node to complete the cycle
        if (!currentCity.equals(startCity) && graph.isEdgeValid(currentCity, startCity)) {
            path.add(startCity);
        } else {
            return null;
        }

        return path;
    }

}
