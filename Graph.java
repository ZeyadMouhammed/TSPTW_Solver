import java.util.*;

public class Graph {

    // Map of city names to their corresponding nodes
    private final Map<String, Node> citiesMap;

    // Constructor to initialize the graph
    public Graph() {
        citiesMap = new HashMap<>();
    }

    // --------- Graph Construction Methods ---------

    /**
     * Adds a city to the graph with its time window.
     *
     * @param name         The name of the city.
     * @param earliestTime The earliest time to visit the city.
     * @param latestTime   The latest time to visit the city.
     */
    public boolean addCity(String name, int earliestTime, int latestTime) {
        if (citiesMap.containsKey(name)) {
            return false; // Do not allow duplicate cities
        }
        if (earliestTime > latestTime) {
            throw new IllegalArgumentException("Earliest time cannot be greater than latest time.");
        }
        citiesMap.put(name, new Node(name, earliestTime, latestTime));
        return true;
    }

    /**
     * Connects two cities in the graph with a bidirectional edge.
     *
     * @param from       The name of the starting city.
     * @param to         The name of the destination city.
     * @param distance   The distance between the cities.
     * @param travelTime The travel time between the cities.
     * @return True if the cities were successfully connected, false otherwise.
     */
    public boolean connectCities(String from, String to, int distance, int travelTime) {

        if (from.equals(to)) {
            return false; // Prevent self-loops
        }

        Node fromNode = getNodeByName(from); // Starting city
        Node toNode = getNodeByName(to);     // Destination city

        if (fromNode != null && toNode != null) {
            if (fromNode.getEdgeTo(toNode) != null || toNode.getEdgeTo(fromNode) != null) {
                return false; // Prevent duplicate edges
            }
            // Add bidirectional edges
            fromNode.addNeighbor(toNode, distance, travelTime);
            toNode.addNeighbor(fromNode, distance, travelTime);
            return true;
        }
        return false;
    }

    //---------- Get Methods -------------

    /**
     * Returns a list of all city names in the graph.
     *
     * @return List of city names.
     */
    public List<String> getAllCities() {
        return new ArrayList<>(citiesMap.keySet());
    }

    /**
     * @return the number of cities in the graph
     */
    public int getNumberOfCities() {
        return citiesMap.size();
    }

    /**
     * Retrieves the travel distance between two cities.
     *
     * @param city1 The name of the first city.
     * @param city2 The name of the second city.
     * @return The travel distance if the edge exists, or -1 if no edge exists.
     */
    public int getEdgeTravelDistance(String city1, String city2) {
        Edge edge = getEdgeBetweenTwoCities(city1, city2);
        if (edge == null) {
            return -1; // Indicate that the edge does not exist
        }
        return edge.distance;
    }

    /**
     * Retrieves the travel time between two cities.
     *
     * @param city1 The name of the first city.
     * @param city2 The name of the second city.
     * @return The travel time if the edge exists, or -1 if no edge exists.
     */
    public int getEdgeTravelTime(String city1, String city2) {
        Edge edge = getEdgeBetweenTwoCities(city1, city2);
        if (edge == null) {
            return -1; // Indicate that the edge does not exist
        }
        return edge.travelTime;
    }

    public int getValidArrivalTime(String city1, String city2, int arrivalTime){
        int totalTime = getEdgeTravelTime(city1, city2) + arrivalTime;
        Node to = getNodeByName(city2);
        if(to.timeWindow.isWithinLatestTimeTimeWindow(totalTime)){
            if(!(to.timeWindow.isWithinEarliestTimeTimeTimeWindow(totalTime))){
                return totalTime + to.timeWindow.earliestTime;
            }else {
                return totalTime;
            }
        }
        return -1;
    }

    /**
     * Retrieves a list of city names that can be reached directly from the given city.
     *
     * @param cityName The name of the city from which to find outgoing edges.
     * @return A list of city names that can be reached directly from the specified city.
     */
    public List<String> getEdgesFromCityName(String cityName) {
        // Get the list of edges (connections) from the city with the specified name
        List<Edge> edges = getEdgesFromCity(cityName);

        // Initialize a list to store the names of cities that can be reached from the given city
        List<String> toCities = new ArrayList<>();

        // Loop through each edge and extract the name of the city that the edge leads to
        for (Edge edge : edges) {
            // Add the name of the city connected by the current edge to the list
            toCities.add(edge.to.name);
        }

        // Return the list of city names that can be reached from the given city
        return toCities;
    }

    public int[] getCityTimeWindow(String from) {
        Node city = getNodeByName(from);
        if (city == null) return new int[]{0, 0};
        return new int[]{city.timeWindow.earliestTime, city.timeWindow.latestTime};
    }

    public int getCityStartTime(String city) {
        return getNodeByName(city).timeWindow.earliestTime;
    }

    public int getCityEndTime(String city) {
        return getNodeByName(city).timeWindow.latestTime;
    }

    // --------- Checking Methods ---------------

    /**
     * @param city1 first city name
     * @param city2 second city name
     * @param time  time to travel between two cities
     * @return is it feasible to travel to this city
     */
    public boolean isEdgeWithinLatestTimeWindow(String city1, String city2, int time) {
        return Objects.requireNonNull(getEdgeBetweenTwoCities(city1, city2)).isEdgeWithinLatestTimeWindow(time);
    }

    /**
     * @param city1 first city name
     * @param city2 second city name
     * @param time  time to travel between two cities
     * @return is it Time WithinEarliestTimeWindow
     */
    public boolean isEdgeWithinEarliestTimeWindow(String city1, String city2, int time) {
        return Objects.requireNonNull(getEdgeBetweenTwoCities(city1, city2)).isEdgeWithinEarliestTimeWindow(time);
    }

    /**
     * checks if edge does exits
     *
     * @param currentCity first city name
     * @param startCity   second city name
     * @return does edge exists or no
     */
    public boolean isEdgeValid(String currentCity, String startCity) {
        return getEdgeBetweenTwoCities(currentCity, startCity) != null;
    }

    // --------- Clear Method -------------------

    public void clearMap() {
        citiesMap.clear();
    }

    // --------- Utility Methods ---------

    /**
     * Calculates the total feasible path cost for a given path, considering travel time and time windows.
     *
     * @param path List of locations representing the path to be evaluated.
     * @return The total cost of the path if feasible, or -1 if the path is invalid due to time window constraints.
     */
    public int[] calculateFeasiblePathCost(List<String> path) {
        int totalCost = 0; // Total travel distance.
        int currentTime = 0; // Current accumulated time.

        // Iterate through each consecutive pair of nodes in the path.
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i);
            String to = path.get(i + 1);

            // Get travel time for the edge.
            int travelTime = getEdgeTravelTime(from, to);
            if (travelTime == -1) {
                // Invalid edge, path is not feasible.
                return null;
            }

            // Calculate arrival time at the destination node.
            int arrivalTime = currentTime + travelTime;

            // Get the time window details for the destination node.
            Node toNode = getNodeByName(to);
            int earliestTime = toNode.timeWindow.earliestTime;
            int latestTime = toNode.timeWindow.latestTime;

            // Check if the path violates the latest time window.
            if (arrivalTime > latestTime) {
                return null; // Path fails latest time window constraint.
            }

            // Wait until the earliest allowed time, if necessary.
            // Proceed with the current arrival time.
            currentTime = Math.max(arrivalTime, earliestTime); // Update to wait until earliest time.

            // Accumulate the cost of the edge.
            totalCost += getEdgeTravelDistance(from, to);
        }

        // Return the total cost and final time as an array.
        return new int[]{totalCost, currentTime};
    }

    /**
     * Calculates the total arrival time for a given path, including an additional travel time.
     * This method validates the connectivity of the path by checking the travel time for each edge.
     *
     * @param path                 The list of nodes representing the path.
     * @param additionalTravelTime The extra travel time to be added after traversing the path.
     * @return The total arrival time, or -1 if the path contains invalid edges.
     */
    public int calculateArrivalTime(List<String> path, int additionalTravelTime) {
        int currentTime = 0; // Keeps track of the cumulative travel time.

        // Iterate through the path to calculate travel time for each edge
        for (int i = 0; i < path.size() - 1; i++) {
            String from = path.get(i); // Starting node of the current edge
            String to = path.get(i + 1); // Ending node of the current edge

            // Get the travel time for the edge from the graph
            int edgeTravelTime = getEdgeTravelTime(from, to);

            // If the edge travel time is -1, it indicates an invalid edge
            if (edgeTravelTime == -1) {
                return -1; // Return -1 for invalid paths
            }

            // Add the edge's travel time to the cumulative travel time
            currentTime += edgeTravelTime;
        }

        // Add the additional travel time to the total cumulative travel time
        return currentTime + additionalTravelTime;
    }

    /**
     * Converts the graph to an adjacency matrix.
     *
     * @return The adjacency matrix.
     */
    public int[][] toAdjacencyMatrix() {
        int n = citiesMap.size();
        int[][] matrix = new int[n][n];
        List<Node> nodeList = new ArrayList<>(citiesMap.values());

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Edge edge = nodeList.get(i).getEdgeTo(nodeList.get(j));
                matrix[i][j] = (edge != null) ? edge.distance : Integer.MAX_VALUE;
            }
        }
        return matrix;
    }

    /**
     * Converts the city's graph into a travel time matrix.
     * The matrix represents the travel times between all pairs of cities.
     *
     * @return A 2D array representing the travel time matrix.
     */
    public int[][] toTravelTimeMatrix() {
        int n = citiesMap.size(); // Get the number of cities (nodes) in the map
        int[][] timeMatrix = new int[n][n]; // Initialize a 2D array for the travel time matrix
        List<Node> nodeList = new ArrayList<>(citiesMap.values()); // Convert the city map to a list for easy access

        // Iterate over all pairs of cities (i, j)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Get the edge (connection) between city i and city j
                Edge edge = nodeList.get(i).getEdgeTo(nodeList.get(j));

                // If an edge exists, assign the travel time to the matrix
                // If no edge exists, assign Integer.MAX_VALUE to represent an unreachable city
                timeMatrix[i][j] = (edge != null) ? edge.travelTime : Integer.MAX_VALUE;
            }
        }

        // Return the populated travel time matrix
        return timeMatrix;
    }

    /**
     * * Converts the Cities time windows to a matrix
     *
     * @return timeWindows in a matrix form
     */
    public int[][] toTimeWindowMatrix() {
        int n = citiesMap.size(); // Get the number of cities in the graph
        int[][] timeWindowMatrix = new int[n][2 * n]; // 2 columns per city: one for earliest, one for latest times
        List<Node> nodeList = new ArrayList<>(citiesMap.values()); // Convert the city map to a list for easy access

        // Iterate over all pairs of cities (i, j)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Node cityNode = nodeList.get(i);
                TimeWindow timeWindow = cityNode.timeWindow; // Get the time window for the current city

                // Assign earliest and latest time of city "i" to the matrix
                timeWindowMatrix[i][2 * j] = timeWindow.earliestTime; // Earliest time for city "i"
                timeWindowMatrix[i][2 * j + 1] = timeWindow.latestTime; // Latest time for city "i"
            }
        }

        // Return the populated time window matrix
        return timeWindowMatrix;
    }

    public String getNearestNeighbor(String current, int currentTime) {
        return getNearestNeighbor(getNodeByName(current), currentTime).name;
    }

    // --------- Private Helper Methods ---------

    /**
     * return a node by its name
     *
     * @param name city name (node name)
     * @return the city node
     */
    private Node getNodeByName(String name) {
        return citiesMap.get(name);
    }

    /**
     * get the Edge between two cities if exist
     *
     * @param city1 name
     * @param city2 name
     * @return the edge if exist
     */
    private Edge getEdgeBetweenTwoCities(String city1, String city2) {
        Node node1 = getNodeByName(city1);
        Node node2 = getNodeByName(city2);

        // Ensure both nodes exist and return the edge between them
        if (node1 != null && node2 != null) {
            return node1.getEdgeTo(node2);
        }
        return null; // No edge exists
    }

    /**
     * get Edges for the city
     *
     * @param cityName name the name of the city
     * @return all it's edges
     */
    private List<Edge> getEdgesFromCity(String cityName) {
        Node node = citiesMap.get(cityName); // Use the map to find the node by city name
        if (node != null) {
            // Return all edges from the given city
            return new ArrayList<>(node.neighbours.values());
        }
        // Return an immutable empty list if the city is not found
        return Collections.emptyList();
    }

    /**
     * @param current     The current Node
     * @param currentTime And it's current time
     * @return nearest city or neighbor city
     */
    private Node getNearestNeighbor(Node current, int currentTime) {
        Node nearest = null;
        int minDistance = Integer.MAX_VALUE;
        int earliestArrivalTime = Integer.MAX_VALUE;  // To store the earliest arrival time in case of ties in distance

        // Iterate over all neighbors to find the nearest one
        for (Edge edge : current.neighbours.values()) {
            int arrivalTime = currentTime + edge.travelTime;

            // Ensure the arrival time is within the time window of the neighbor
            if (edge.isEdgeWithinLatestTimeWindow(arrivalTime)) {
                // First, check if this edge has a smaller distance
                if (edge.distance < minDistance) {
                    nearest = edge.to;  // Update the nearest node
                    minDistance = edge.distance;  // Update the minimum distance
                    earliestArrivalTime = arrivalTime;  // Update the earliest arrival time
                }
                // If distances are the same, prefer the one with the earlier arrival time
                else if (edge.distance == minDistance && arrivalTime < earliestArrivalTime) {
                    nearest = edge.to;  // Update the nearest node with earlier arrival time
                    earliestArrivalTime = arrivalTime;  // Update the earliest arrival time
                }
            }
        }

        // Return the nearest node, or null if no valid neighbor was found
        return nearest;
    }

    // --------- Update Methods ---------

    /**
     * Updates the name of an existing city.
     *
     * @param oldName The current name of the city.
     * @param newName The new name for the city.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateCityName(String oldName, String newName) {
        if (!citiesMap.containsKey(oldName)) {
            return false; // Old city name does not exist
        }
        if (citiesMap.containsKey(newName)) {
            return false; // New city name already exists
        }

        Node city = citiesMap.remove(oldName); // Remove the old name
        city.name = newName; // Update the name
        citiesMap.put(newName, city); // Add with the new name
        return true;
    }

    /**
     * Updates the time window for a city.
     *
     * @param cityName        The name of the city.
     * @param newEarliestTime The new earliest time.
     * @param newLatestTime   The new latest time.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateTimeWindow(String cityName, int newEarliestTime, int newLatestTime) {
        if (!citiesMap.containsKey(cityName)) {
            return false; // City does not exist
        }
        if (newEarliestTime > newLatestTime) {
            throw new IllegalArgumentException("Earliest time cannot be greater than latest time.");
        }

        Node city = citiesMap.get(cityName);
        city.timeWindow.earliestTime = newEarliestTime;
        city.timeWindow.latestTime = newLatestTime;
        return true;
    }

    /**
     * Updates the edge values (distance and travel time) between two cities.
     *
     * @param fromCityName  The starting city name.
     * @param toCityName    The destination city name.
     * @param newDistance   The new distance.
     * @param newTravelTime The new travel time.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateEdgeValues(String fromCityName, String toCityName, int newDistance, int newTravelTime) {
        if (newDistance < 0 || newTravelTime < 0) {
            throw new IllegalArgumentException("Distance and travel time must be non-negative.");
        }

        Node fromCity = citiesMap.get(fromCityName);
        Node toCity = citiesMap.get(toCityName);

        if (fromCity == null || toCity == null) {
            return false; // Either city does not exist
        }

        Edge edgeFromTo = fromCity.getEdgeTo(toCity);
        Edge edgeToFrom = toCity.getEdgeTo(fromCity);

        if (edgeFromTo == null || edgeToFrom == null) {
            return false; // Edge does not exist
        }

        edgeFromTo.distance = newDistance;
        edgeFromTo.travelTime = newTravelTime;
        edgeToFrom.distance = newDistance;
        edgeToFrom.travelTime = newTravelTime;
        return true;
    }

    /**
     * Updates an edge to point to a new destination city.
     *
     * @param fromCityName  The starting city name.
     * @param oldToCityName The current destination city name.
     * @param newToCityName The new destination city name.
     * @param newDistance   The new distance.
     * @param newTravelTime The new travel time.
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateEdgeTo(String fromCityName, String oldToCityName, String newToCityName, int newDistance, int newTravelTime) {
        if (fromCityName.equals(newToCityName)) {
            return false; // Prevent self-loops
        }
        if (newDistance < 0 || newTravelTime < 0) {
            throw new IllegalArgumentException("Distance and travel time must be non-negative.");
        }

        Node fromCity = citiesMap.get(fromCityName);
        Node oldToCity = citiesMap.get(oldToCityName);
        Node newToCity = citiesMap.get(newToCityName);

        if (fromCity == null || oldToCity == null || newToCity == null) {
            return false; // One or more cities do not exist
        }

        Edge edgeFromTo = fromCity.getEdgeTo(oldToCity);
        if (edgeFromTo == null) {
            return false; // Edge does not exist
        }

        fromCity.neighbours.remove(oldToCity); // Remove the old edge
        fromCity.addNeighbor(newToCity, newDistance, newTravelTime);

        oldToCity.neighbours.remove(fromCity); // Remove reverse edge
        newToCity.addNeighbor(fromCity, newDistance, newTravelTime);

        return true;
    }

    // --------- Nested Classes ---------

    // Node class representing a city in the graph
    private static class Node {
        private String name; // City name
        private TimeWindow timeWindow; // Time window for visiting
        Map<Node, Edge> neighbours; // Adjacent cities with their edges

        public Node(String name, int earliest, int latest) {
            this.name = name;
            this.timeWindow = new TimeWindow(earliest, latest);
            this.neighbours = new HashMap<>();
        }

        private void addNeighbor(Node neighbour, int distance, int travelTime) {
            if (neighbour == null || distance < 0 || travelTime < 0) {
                throw new IllegalArgumentException("Invalid neighbor or edge values.");
            }
            neighbours.put(neighbour, new Edge(neighbour, distance, travelTime));
        }

        private Edge getEdgeTo(Node neighbour) {
            return neighbours.getOrDefault(neighbour, null);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return name.equals(node.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return "City {name='" + name + "', timeWindow=[" + timeWindow.earliestTime + ", " + timeWindow.latestTime + "]}";
        }

    }

    // Edge class representing a connection between two cities
    private static class Edge {
        private Node to; // Destination city
        private int distance;
        private int travelTime;

        private Edge(Node to, int distance, int travelTime) {
            if (to == null || distance < 0 || travelTime < 0) {
                throw new IllegalArgumentException("Invalid edge values.");
            }
            this.to = to;
            this.distance = distance;
            this.travelTime = travelTime;
        }

        private boolean isEdgeWithinLatestTimeWindow(int time) {
            return to.timeWindow.isWithinLatestTimeTimeWindow(time);
        }

        private boolean isEdgeWithinEarliestTimeWindow(int time) {
            return to.timeWindow.isWithinEarliestTimeTimeTimeWindow(time);
        }

        private int getEarliestTime() {
            return to.timeWindow.earliestTime;
        }

        @Override
        public String toString() {
            return "to " + to.name + " [distance=" + distance + ", travelTime=" + travelTime + "]";
        }
    }

    // TimeWindow class representing the earliest and latest visit times for a city
    private static class TimeWindow {
        private int earliestTime;
        private int latestTime;

        private TimeWindow(int earliestTime, int latestTime) {
            if (earliestTime > latestTime) {
                throw new IllegalArgumentException("Earliest time cannot be greater than latest time.");
            }
            this.earliestTime = earliestTime;
            this.latestTime = latestTime;
        }

        private boolean isWithinLatestTimeTimeWindow(int time) {
            return time <= latestTime;
        }

        private boolean isWithinEarliestTimeTimeTimeWindow(int time) {
            return time >= earliestTime;
        }

    }

}