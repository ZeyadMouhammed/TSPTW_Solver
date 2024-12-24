import java.util.*;

public class Greedy {

    /**
     * Solve TSPTW using Greedy algorithm
     *
     * @param graph     The graph representing the TSPTW problem
     * @param startCity The starting node
     *
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
            String nextNode = null;
            int minDistance = Integer.MAX_VALUE;
            int minTravelTime = Integer.MAX_VALUE;

            // Check all cities that are not visited
            for (String city : graph.getAllCities()) {
                if (!visited.contains(city)) {
                    // Check if an edge exists between the current city and the next city
                    if (graph.isEdgeValid(currentCity, city)) {
                        // Get the travel time and distance
                        int travelTime = graph.getEdgeTravelTime(currentCity, city);
                        int travelDistance = graph.getEdgeTravelDistance(currentCity, city); // Road length

                        // Get the valid arrival time based on the current time and travel time
                        int arrivalTime = graph.getValidArrivalTime(currentCity, city, currentTime);

                        if (arrivalTime != -1) { // If the arrival time is valid
                            // Select the city with the smallest distance, prioritize by distance first
                            if (travelDistance < minDistance ||
                                    (travelDistance == minDistance && travelTime < minTravelTime)) {
                                minDistance = travelDistance;
                                minTravelTime = travelTime;
                                nextNode = city;
                                currentTime = arrivalTime; // Update the current time after arriving
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
            totalDistance += minDistance; // Add the distance to the total

            // Update the current city and time after visiting the next node
            currentCity = nextNode;
        }

        // Optionally return to the start node to complete the cycle
        if (!currentCity.equals(startCity) && graph.isEdgeValid(currentCity, startCity)) {
            path.add(startCity);
            totalDistance += graph.getEdgeTravelDistance(currentCity, startCity); // Add distance back to the start city
        } else {
            return null;
        }

        return path;
    }


}
