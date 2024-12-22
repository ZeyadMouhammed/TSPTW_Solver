import java.util.Arrays;

public class DynamicProgramming {

    // Method to solve TSPTW using cost and travel time matrices
    public static int[] SolveTSPTW_DP(int[][] costMatrix, int[][] travelTimeMatrix, int[][] timeWindows) {
        int n = costMatrix.length;
        int fullMask = (1 << n) - 1;

        // dp[mask][i] -> Minimum cost to visit all nodes in 'mask' ending at node 'i'
        int[][] dp = new int[1 << n][n];
        int[][] arrivalTime = new int[1 << n][n]; // Tracks arrival time at each node

        // Initialize dp and arrivalTime arrays
        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE);
        for (int[] row : arrivalTime) Arrays.fill(row, Integer.MAX_VALUE);

        // Base case: start at node 0 (assuming node 0 is the starting node)
        dp[1][0] = 0;
        arrivalTime[1][0] = 0;

        // Iterate through all subsets of nodes
        for (int mask = 1; mask <= fullMask; mask++) {
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) == 0) continue; // Node 'i' not in subset

                for (int j = 0; j < n; j++) {
                    if ((mask & (1 << j)) != 0 || i == j) continue; // Node 'j' already visited or same node

                    int newMask = mask | (1 << j);
                    int travelTime = travelTimeMatrix[i][j];

                    if (travelTime == Integer.MAX_VALUE) continue; // Skip invalid paths

                    int newArrivalTime = Math.max(arrivalTime[mask][i] + travelTime, timeWindows[j][0]);

                    // Check time window constraint
                    if (newArrivalTime <= timeWindows[j][1]) {
                        int newCost = dp[mask][i] + costMatrix[i][j];

                        if (dp[mask][i] != Integer.MAX_VALUE && costMatrix[i][j] != Integer.MAX_VALUE && dp[newMask][j] > newCost) {
                            dp[newMask][j] = newCost;
                            arrivalTime[newMask][j] = newArrivalTime;
                        }
                    }
                }
            }
        }

        // To store the result: minimum cost and corresponding total time
        int minCost = Integer.MAX_VALUE;
        int totalTime = Integer.MAX_VALUE;

        // Find minimum cost and corresponding time
        for (int i = 1; i < n; i++) {
            if (dp[fullMask][i] != Integer.MAX_VALUE && costMatrix[i][0] != Integer.MAX_VALUE) {
                minCost = Math.min(minCost, dp[fullMask][i] + costMatrix[i][0]);

                // Find the corresponding total time for the optimal path
                if (dp[fullMask][i] != Integer.MAX_VALUE) {
                    totalTime = Math.min(totalTime, arrivalTime[fullMask][i] + travelTimeMatrix[i][0]);
                }
            }
        }

        // Return both the minimum cost and corresponding time
        return new int[] { minCost == Integer.MAX_VALUE ? -1 : minCost, totalTime == Integer.MAX_VALUE ? -1 : totalTime };
    }
}
