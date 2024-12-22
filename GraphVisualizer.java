import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphVisualizer extends JPanel {

    private Graph graph;

    public GraphVisualizer(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(800, 600)); // Set the preferred size of the panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set the font for rendering city names and time window text
        g.setFont(new Font("Arial", Font.PLAIN, 12));

        // Get the list of cities and prepare layout
        List<String> cityNames = graph.getAllCities();
        int radius = 20; // Radius of the circle representing the city
        int angleStep = 360 / cityNames.size();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radiusOffset = 200; // Distance from the center to place the cities

        // Set colors for drawing
        Color cityColor = Color.BLACK;
        Color timeWindowColor = Color.YELLOW;
        Color edgeCostColor = Color.YELLOW;
        Color edgeColor = Color.GRAY;
        Color timeWindowTextColor = Color.BLACK;
        Color costTextColor = Color.BLACK;
        Color cityTextColor = Color.WHITE;

        // Map city names to their positions on the circle
        Map<String, Point> cityPositions = new HashMap<>();
        FontMetrics metrics = g.getFontMetrics();

        // Draw cities and time windows
        for (int i = 0; i < cityNames.size(); i++) {
            String cityName = cityNames.get(i);
            int angle = i * angleStep;
            int x = (int) (centerX + radiusOffset * Math.cos(Math.toRadians(angle)) - radius);
            int y = (int) (centerY + radiusOffset * Math.sin(Math.toRadians(angle)) - radius);
            cityPositions.put(cityName, new Point(x, y));

            // Draw the city (circle)
            g.setColor(cityColor);
            g.fillOval(x, y, radius * 2, radius * 2);

            // Display the time window above or below the city based on its position
            int[] timeWindow = graph.getCityTimeWindow(cityName);
            String timeWindowText = "TW: [" + timeWindow[0] + ", " + timeWindow[1] + "]";

            // Get dimensions of the time window text
            int timeWindowWidth = metrics.stringWidth(timeWindowText);
            int timeWindowHeight = metrics.getHeight();

            // Calculate the initial position for the time window background
            int timeWindowX = x + radius - timeWindowWidth / 2;

            // Check if the city is in the lower half of the window (y position greater than half of the window height)
            int timeWindowY;
            if (y > getHeight() / 2) {
                // Place the time window below the city circle
                timeWindowY = y + radius * 2 + 10;
            } else {
                // Place the time window above the city circle
                timeWindowY = y - timeWindowHeight - 10;
            }

            // Draw the time window background
            g.setColor(timeWindowColor);
            g.fillRect(timeWindowX, timeWindowY, timeWindowWidth, timeWindowHeight);

            // Draw the time window text
            g.setColor(timeWindowTextColor);
            g.drawString(timeWindowText, timeWindowX, timeWindowY + metrics.getAscent()); // Align text inside the yellow box

            // Display the city name inside the circle
            int nameWidth = metrics.stringWidth(cityName);
            int nameHeight = metrics.getHeight();

            // Centering the city name horizontally and vertically
            int nameX = x + (radius * 2 - nameWidth) / 2;
            int nameY = y + radius + (nameHeight / 2);

            g.setColor(cityTextColor);
            g.drawString(cityName, nameX, nameY); // Center the city name inside the circle
        }

        // Draw the edges between cities (ensure grey edges are drawn)
        for (String cityName : cityNames) {
            List<String> connectedCities = graph.getEdgesFromCityName(cityName);
            for (String destination : connectedCities) {
                Point cityPos = cityPositions.get(cityName);
                Point destPos = cityPositions.get(destination);

                if (cityPos != null && destPos != null) {
                    // Get the distance and travel time for the edge
                    int distance = graph.getEdgeTravelDistance(cityName, destination);
                    int travelTime = graph.getEdgeTravelTime(cityName, destination);

                    // Draw the edge (line) between the cities
                    int dx = destPos.x - cityPos.x; // Change in x between the two cities
                    int dy = destPos.y - cityPos.y; // Change in y between the two cities
                    double distanceCityCenter = Math.sqrt(dx * dx + dy * dy); // Distance between city centers

                    // Calculate the direction vector, normalized to unit length
                    double unitDx = dx / distanceCityCenter;
                    double unitDy = dy / distanceCityCenter;

                    // Offset the starting and ending points to the edges of the circles
                    int startX = (int) (cityPos.x + radius + radius * unitDx);
                    int startY = (int) (cityPos.y + radius + radius * unitDy);
                    int endX = (int) (destPos.x + radius - radius * unitDx);
                    int endY = (int) (destPos.y + radius - radius * unitDy);

                    // Draw the line from the edge of the source circle to the edge of the destination circle
                    g.setColor(edgeColor);
                    g.drawLine(startX, startY, endX, endY);

                    // Calculate the middle of the edge to display cost information
                    int midX = (startX + endX) / 2;
                    int midY = (startY + endY) / 2;

                    // Offset the label slightly above or to the side of the edge
                    int labelOffset = 10; // Adjust the offset as needed
                    midX += labelOffset; // Move label slightly to the right
                    midY -= labelOffset; // Move label slightly above

                    // Display the cost information (travel time and distance)
                    String edgeLabel = "[ T: " + travelTime + " D: " + distance + " ]";

                    // Draw a background rectangle for the text
                    int textWidth = metrics.stringWidth(edgeLabel);
                    int textHeight = metrics.getHeight();
                    g.setColor(edgeCostColor); // Background color for the text
                    g.fillRect(midX - textWidth / 2, midY - textHeight / 2, textWidth, textHeight);

                    // Draw the text over the background
                    g.setColor(costTextColor); // Text color
                    g.drawString(edgeLabel, midX - textWidth / 2, midY + textHeight / 4);
                }
            }
        }
    }


    public static void createAndShowGUI(Graph graph) {
        JFrame frame = new JFrame("Graph Visualization");
        GraphVisualizer panel = new GraphVisualizer(graph);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

}