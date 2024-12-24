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
        int totalDistance = 0;

        // Add the start node to the path and mark it visited
        path.add(currentCity);
        visited.add(currentCity);

        // While there are unvisited nodes
        while (visited.size() < graph.getNumberOfCities()) {
            String nextNode = null;
            int minDistance = Integer.MAX_VALUE;
            int minTravelTime = Integer.MAX_VALUE;

            // Check all cities that are not visited
            for (String city : graph.getAllCities()) {
                if (!visited.contains(city)) {
                    // Check if an edge exists between current city and next city
                    if (graph.isEdgeValid(currentCity, city)) {
                        // Calculate travel time and arrival time
                        int travelTime = graph.getEdgeTravelTime(currentCity, city);
                        int travelDistance = graph.getEdgeTravelDistance(currentCity, city); // Get the road length
                        int arrivalTime = currentTime + travelTime;

                        // Use isEdgeWithinTimeWindow to check if the city is feasible to visit
                        if (graph.isEdgeWithinLatestTimeWindow(currentCity, city, arrivalTime)) {
                            // Select the city with the smallest distance, prioritize by distance first
                            if (travelDistance < minDistance ||
                                    (travelDistance == minDistance && travelTime < minTravelTime)) {
                                minDistance = travelDistance;
                                minTravelTime = travelTime;
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

            // Update path, visited cities, time, and distance
            visited.add(nextNode);
            path.add(nextNode);
            currentCity = nextNode;
            currentTime += minTravelTime; // Update the time after visiting the city
            totalDistance += minDistance; // Update the distance after visiting the city
        }

        // Optionally return to the start node to complete the cycle
        if (!currentCity.equals(startCity) && graph.isEdgeValid(currentCity, startCity)) {
            path.add(startCity);
            totalDistance += graph.getEdgeTravelDistance(currentCity, startCity); // Add distance back to start city
        } else {
            return null;
        }

        System.out.println("Total Distance: " + totalDistance); // Debug or use this as needed
        return path;
    }


}
