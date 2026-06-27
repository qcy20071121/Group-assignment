package com.weeee;

import java.util.*;

public class EnemyGraph {
    static List<List<Integer>> adj;
    static int start = 1;

    static List<List<Edge>> graph;

    static final double[][] nodeCoords = {
        {0.0, 0.0},         // 0-indexed unused
        {120.0, 250.0},     // Node 1 (Camp)
        {300.0, 150.0},     // Node 2
        {270.0, 40.0},      // Node 3
        {380.0, 90.0},      // Node 4
        {570.0, 100.0},     // Node 5
        {470.0, 200.0},     // Node 6
        {670.0, 100.0},     // Node 7
        {600.0, 280.0},     // Node 8
        {720.0, 310.0},     // Node 9
        {370.0, 380.0}      // Node 10
    };

    static class Edge {
        int to;
        int distance;
        String terrain;
        
        public Edge(int to, int distance, String terrain) {
            this.to = to;
            this.distance = distance;
            this.terrain = terrain;
        }
    }

    public static void build() {
        adj = new ArrayList<>();
        graph = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            adj.add(new ArrayList<>());
            graph.add(new ArrayList<>());
        }
        
        // Flat road:
        addEdge(1, 6, 20, "flat");
        addEdge(1, 3, 18, "flat");
        addEdge(5, 6, 17, "flat");
        addEdge(7, 8, 19, "flat");
        addEdge(7, 9, 17, "flat");
        addEdge(1, 10, 16, "flat");
        addEdge(9, 10, 18, "flat");

        // Forest:
        addEdge(1, 2, 10, "forest");
        addEdge(5, 7, 10, "forest");
        addEdge(6, 7, 23, "forest");
        addEdge(8, 10, 12, "forest");

        // Swamp:
        addEdge(2, 4, 10, "swamp");
        addEdge(3, 4, 12, "swamp");
        addEdge(4, 5, 12, "swamp");
        addEdge(8, 9, 7, "swamp");

        // Plank road:
        addEdge(6, 8, 35, "plank");
        
        // 3 -> 7
        graph.get(3).add(new Edge(7, 28, "plank"));
        adj.get(3).add(7);

        for (int i = 1; i <= 10; i++) {
            Collections.sort(adj.get(i));
        }
    }

    private static void addEdge(int u, int v, int distance, String terrain) {
        graph.get(u).add(new Edge(v, distance, terrain));
        graph.get(v).add(new Edge(u, distance, terrain));
        
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    public static void bfs(int target) {

        int[] dist = new int[11];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Queue<Integer> distQ = new LinkedList<>();
        dist[start] = 0;
        distQ.add(start);
        while (!distQ.isEmpty()) {
            int u = distQ.poll();
            for (int v : adj.get(u)) {
                if (dist[v] == Integer.MAX_VALUE) {
                    dist[v] = dist[u] + 1;
                    distQ.add(v);
                }
            }
        }

        Queue<List<Integer>> q = new LinkedList<>();
        q.add(Arrays.asList(start));
        List<List<Integer>> shortestPaths = new ArrayList<>();

        while (!q.isEmpty()) {
            List<Integer> path = q.poll();
            int current = path.get(path.size() - 1);
            if (current == target) {
                shortestPaths.add(path);
                continue;
            }
            if (path.size() - 1 >= dist[target]) {
                continue;
            }
            for (int next : adj.get(current)) {
                if (dist[next] == dist[current] + 1) {
                    List<Integer> newPath = new ArrayList<>(path);
                    newPath.add(next);
                    q.add(newPath);
                }
            }
        }

        System.out.println("Best path:");
        for (List<Integer> p : shortestPaths) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < p.size(); i++) {
                sb.append(p.get(i));
                if (i < p.size() - 1) sb.append("-> ");
            }
            System.out.println(sb.toString());
        }
    }

    public static void dijkstra(Officer officer, int target) {
        if (target < 1 || target > 10) {
            System.out.println("Invalid target node.");
            return;
        }

        double baseSpeed = officer.type.equalsIgnoreCase("cavalry") ? 2.0 : 1.0;
        
        double[] minTime = new double[11];
        Arrays.fill(minTime, Double.MAX_VALUE);
        int[] parent = new int[11];
        Arrays.fill(parent, -1);

        PriorityQueue<double[]> pq = new PriorityQueue<>(new Comparator<double[]>() {
            @Override
            public int compare(double[] a, double[] b) {
                return Double.compare(a[1], b[1]);
            }
        });
        minTime[start] = 0.0;
        pq.add(new double[]{start, 0.0});

        while (!pq.isEmpty()) {
            double[] curr = pq.poll();
            int u = (int) curr[0];
            double t = curr[1];

            if (t > minTime[u]) continue;
            if (u == target) break;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double mult = getMultiplier(officer.type, edge.terrain);
                double speed = baseSpeed * mult;
                double travelTime = edge.distance / speed;

                if (minTime[u] + travelTime < minTime[v]) {
                    minTime[v] = minTime[u] + travelTime;
                    parent[v] = u;
                    pq.add(new double[]{v, minTime[v]});
                }
            }
        }

        if (minTime[target] == Double.MAX_VALUE) {
            System.out.println("No path found to " + target + " for " + officer.name);
            return;
        }

        List<Integer> path = new ArrayList<>();
        int currNode = target;
        while (currNode != -1) {
            path.add(currNode);
            currNode = parent[currNode];
        }
        Collections.reverse(path);

        System.out.println("\n--- Shortest Time Path for " + officer.name + " (" + officer.type + ") ---");
        StringBuilder pathSb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            pathSb.append(path.get(i));
            if (i < path.size() - 1) {
                pathSb.append(" -> ");
            }
        }
        System.out.println("Path: " + pathSb.toString());
        System.out.printf("Time: %.2f hours\n", minTime[target]);
    }

    private static double getMultiplier(String troopType, String terrain) {
        String t = troopType.toLowerCase();
        String m = terrain.toLowerCase();
        if (t.contains("cavalry")) {
            switch (m) {
                case "flat": return 3.0;
                case "forest": return 0.8;
                case "swamp": return 0.3;
                case "plank": return 0.5;
                default: return 1.0;
            }
        } else if (t.contains("archer")) {
            switch (m) {
                case "flat": return 2.0;
                case "forest": return 1.0;
                case "swamp": return 2.5;
                case "plank": return 0.5;
                default: return 1.0;
            }
        } else if (t.contains("infantry")) {
            switch (m) {
                case "flat": return 2.0;
                case "forest": return 2.5;
                case "swamp": return 1.0;
                case "plank": return 0.5;
                default: return 1.0;
            }
        }
        return 1.0;
    }

    private static double getEuclideanDistance(int u, int v) {
        double dx = nodeCoords[u][0] - nodeCoords[v][0];
        double dy = nodeCoords[u][1] - nodeCoords[v][1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    static class AStarNode implements Comparable<AStarNode> {
        int id;
        double g; // Actual time elapsed
        double f; // Total estimated cost (g + h)
        
        public AStarNode(int id, double g, double f) {
            this.id = id;
            this.g = g;
            this.f = f;
        }
        
        @Override
        public int compareTo(AStarNode o) {
            return Double.compare(this.f, o.f);
        }
    }

    public static void aStar(Officer officer, int target) {
        if (target < 1 || target > 10) {
            System.out.println("Invalid target node.");
            return;
        }

        double baseSpeed = officer.type.equalsIgnoreCase("cavalry") ? 2.0 : 1.0;
        double maxSpeed = baseSpeed * 3.0; // max possible speed modifier is flat road (x3)

        double[] minTime = new double[11];
        Arrays.fill(minTime, Double.MAX_VALUE);
        int[] parent = new int[11];
        Arrays.fill(parent, -1);

        PriorityQueue<AStarNode> pq = new PriorityQueue<>();
        minTime[start] = 0.0;
        double hStart = getEuclideanDistance(start, target) / maxSpeed;
        pq.add(new AStarNode(start, 0.0, hStart));

        while (!pq.isEmpty()) {
            AStarNode curr = pq.poll();
            int u = curr.id;
            double g = curr.g;

            if (g > minTime[u]) continue;
            if (u == target) break;

            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                double mult = getMultiplier(officer.type, edge.terrain);
                double speed = baseSpeed * mult;
                double travelTime = edge.distance / speed;

                if (minTime[u] + travelTime < minTime[v]) {
                    minTime[v] = minTime[u] + travelTime;
                    parent[v] = u;
                    double h = getEuclideanDistance(v, target) / maxSpeed;
                    pq.add(new AStarNode(v, minTime[v], minTime[v] + h));
                }
            }
        }

        if (minTime[target] == Double.MAX_VALUE) {
            System.out.println("No path found to " + target + " for " + officer.name);
            return;
        }

        List<Integer> path = new ArrayList<>();
        int currNode = target;
        while (currNode != -1) {
            path.add(currNode);
            currNode = parent[currNode];
        }
        Collections.reverse(path);

        System.out.println("\n--- A* Search Path (Shortest Time) for " + officer.name + " (" + officer.type + ") ---");
        StringBuilder pathSb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            pathSb.append(path.get(i));
            if (i < path.size() - 1) {
                pathSb.append(" -> ");
            }
        }
        System.out.println("Path: " + pathSb.toString());
        System.out.printf("Time: %.2f hours\n", minTime[target]);
    }

    static class GBFSNode implements Comparable<GBFSNode> {
        int id;
        double h; // Euclidean distance heuristic
        
        public GBFSNode(int id, double h) {
            this.id = id;
            this.h = h;
        }
        
        @Override
        public int compareTo(GBFSNode o) {
            return Double.compare(this.h, o.h);
        }
    }

    public static void bestFirstSearch(int target) {
        if (target < 1 || target > 10) {
            System.out.println("Invalid target node.");
            return;
        }

        PriorityQueue<GBFSNode> pq = new PriorityQueue<>();
        boolean[] visited = new boolean[11];
        int[] parent = new int[11];
        Arrays.fill(parent, -1);

        pq.add(new GBFSNode(start, getEuclideanDistance(start, target)));
        visited[start] = true;

        while (!pq.isEmpty()) {
            GBFSNode curr = pq.poll();
            int u = curr.id;

            if (u == target) break;

            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    pq.add(new GBFSNode(v, getEuclideanDistance(v, target)));
                }
            }
        }

        if (!visited[target]) {
            System.out.println("No path found to " + target + " using Best First Search.");
            return;
        }

        List<Integer> path = new ArrayList<>();
        int currNode = target;
        while (currNode != -1) {
            path.add(currNode);
            currNode = parent[currNode];
        }
        Collections.reverse(path);

        System.out.println("\n--- Best First Search Path (Greedy Distance) ---");
        StringBuilder pathSb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            pathSb.append(path.get(i));
            if (i < path.size() - 1) {
                pathSb.append(" -> ");
            }
        }
        System.out.println("Path: " + pathSb.toString());
        System.out.println("Steps: " + (path.size() - 1));
    }
}