package com.weeee;

import java.util.*;

public class FireCluster {
    static int[][] grid;
    static boolean[][] vis;
    static int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
    static int[] dy = {-1, 0, 1, -1, 1, -1, 0, 1};

    static int[][] defaultMatrix = {
        {1, 1, 0, 0, 1, 0, 0, 1, 1, 1},
        {1, 0, 0, 0, 1, 0, 0, 0, 1, 0},
        {1, 0, 1, 1, 1, 0, 1, 0, 1, 0},
        {1, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {1, 0, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 0, 1, 1, 0, 1, 0},
        {1, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {1, 0, 0, 0, 1, 0, 1, 1, 1, 1},
        {1, 0, 0, 0, 1, 0, 0, 0, 0, 0}
    };

    public static void readMatrix(Scanner sc) {
        System.out.print("Please enter the number of rows in the matrix: ");
        int rows = sc.nextInt();
        System.out.print("Please enter the number of columns in the matrix: ");
        int cols = sc.nextInt();

        grid = new int[rows][cols];
        vis = new boolean[rows][cols];

        System.out.println("Please enter the matrix (Each line has " + cols + " numbers, 0 or 1):");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = sc.nextInt();
            }
        }
    }
    
    private static void dfs(int i, int j, List<int[]> currentCluster) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] == 0 || vis[i][j])
            return;
        vis[i][j] = true;
        currentCluster.add(new int[]{i, j});
        for (int d = 0; d < 8; d++) {
            dfs(i + dx[d], j + dy[d], currentCluster);
        }
    }
    
    public static String count(Scanner sc) {
        System.out.println("\n--- Fire Clusters (Basic) ---");
        System.out.println("1. Use Default 10x10 Matrix");
        System.out.println("2. Enter Custom Matrix");
        System.out.print("Choose: ");
        int choice = sc.nextInt();
        sc.nextLine(); // consume newline
        
        if (choice == 1) {
            grid = defaultMatrix;
            vis = new boolean[defaultMatrix.length][defaultMatrix[0].length];
        } else {
            readMatrix(sc);
        }

        int cnt = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1 && !vis[i][j]) {
                    List<int[]> cluster = new ArrayList<>();
                    dfs(i, j, cluster);
                    cnt++;
                }
            }
        }
        System.out.println("Number of battleship clusters: " + cnt);
        return "Number of battleship clusters: " + cnt;
    }

    public static void findOptimizedPoints(Scanner sc) {
        System.out.println("\n--- Red Cliff on Fire with Optimized Points ---");
        System.out.println("1. Use Default 10x10 Matrix");
        System.out.println("2. Enter Custom Matrix");
        System.out.print("Choose: ");
        int choice = sc.nextInt();
        sc.nextLine(); // consume newline
        
        if (choice == 1) {
            grid = defaultMatrix;
            vis = new boolean[defaultMatrix.length][defaultMatrix[0].length];
        } else {
            readMatrix(sc);
        }

        List<List<int[]>> clusters = new ArrayList<>();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1 && !vis[i][j]) {
                    List<int[]> cluster = new ArrayList<>();
                    dfs(i, j, cluster);
                    clusters.add(cluster);
                }
            }
        }

        System.out.println("Number of battleship clusters found: " + clusters.size());
        for (int cIdx = 0; cIdx < clusters.size(); cIdx++) {
            List<int[]> cluster = clusters.get(cIdx);
            
            int minMaxCycles = Integer.MAX_VALUE;
            int[] bestCoord = null;
            
            Set<String> clusterSet = new HashSet<>();
            for (int[] p : cluster) {
                clusterSet.add(p[0] + "," + p[1]);
            }
            
            for (int[] startNode : cluster) {
                int cycles = getBfsCycles(startNode, clusterSet);
                if (cycles < minMaxCycles) {
                    minMaxCycles = cycles;
                    bestCoord = startNode;
                } else if (cycles == minMaxCycles) {
                    if (bestCoord == null || startNode[0] < bestCoord[0] || (startNode[0] == bestCoord[0] && startNode[1] < bestCoord[1])) {
                        bestCoord = startNode;
                    }
                }
            }
            
            System.out.print("Cluster " + (cIdx + 1) + " (size: " + cluster.size() + "): ");
            if (bestCoord != null) {
                System.out.println("Optimal Coordinate: [" + bestCoord[0] + ", " + bestCoord[1] + "], Burn Cycles: " + minMaxCycles);
            } else {
                System.out.println("No optimal coordinate found.");
            }
        }
    }

    private static int getBfsCycles(int[] start, Set<String> clusterSet) {
        Queue<int[]> q = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        
        q.add(new int[]{start[0], start[1], 0});
        visited.add(start[0] + "," + start[1]);
        
        int maxDepth = 0;
        
        while (!q.isEmpty()) {
            int[] curr = q.poll();
            int cx = curr[0];
            int cy = curr[1];
            int depth = curr[2];
            maxDepth = Math.max(maxDepth, depth);
            
            for (int d = 0; d < 8; d++) {
                int nx = cx + dx[d];
                int ny = cy + dy[d];
                String key = nx + "," + ny;
                
                if (clusterSet.contains(key) && !visited.contains(key)) {
                    visited.add(key);
                    q.add(new int[]{nx, ny, depth + 1});
                }
            }
        }
        return maxDepth;
    }
}