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
        int currentTime = 0;  // Start time is 0
        int totalDistance = 0;

        // Add the start node to the path and mark it visited
        path.add(currentCity);
        visited.add(currentCity);

        // While there are unvisited cities
        while (visited.size() < graph.getNumberOfCities()) {
            String nextCity = null;
            int minDistance = Integer.MAX_VALUE;
            int bestArrivalTime = Integer.MAX_VALUE;

            // Check all cities that are not visited
            for (String city : graph.getAllCities()) {
                if (!visited.contains(city) && graph.isEdgeValid(currentCity, city)) {
                    int validArrivalTime = graph.getValidArrivalTime(currentCity, city, currentTime);

                    if (validArrivalTime != -1) {
                        int travelDistance = graph.getEdgeTravelDistance(currentCity, city);

                        // Select the next city based on smallest distance and smallest valid arrival time in case of a tie
                        if (travelDistance < minDistance ||
                                (travelDistance == minDistance && validArrivalTime < bestArrivalTime)) {
                            minDistance = travelDistance;
                            nextCity = city;
                            bestArrivalTime = validArrivalTime;
                        }
                    }
                }
            }

            // If no valid next city is found
            if (nextCity == null) {
                return null;
            }

            // Update path, visited cities, total distance, and current city/time
            visited.add(nextCity);
            path.add(nextCity);
            totalDistance += minDistance;
            currentCity = nextCity;
            currentTime = bestArrivalTime;
        }

        // Attempt to return to the start node
        if (graph.isEdgeValid(currentCity, startCity)) {
            int returnDistance = graph.getEdgeTravelDistance(currentCity, startCity);
            int returnArrivalTime = graph.getValidArrivalTime(currentCity, startCity, currentTime);

            if (returnArrivalTime != -1) {
                path.add(startCity);
                totalDistance += returnDistance;
            } else {
                return null;
            }
        } else {
            return null;
        }

        return path;
    }

}
