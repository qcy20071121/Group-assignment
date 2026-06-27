package com.weeee;

import java.util.*;

public class FoodDFS {
    static List<List<Integer>> adj;
    static Set<Integer> foodNodes = new HashSet<>();
    static List<List<Integer>> validCycles = new ArrayList<>();

    public static void build() {
        adj = EnemyGraph.adj;
    }

    public static void findPath(Set<Integer> noFood) {
        foodNodes.clear();
        validCycles.clear();

        for (int i = 2; i <= 10; i++) {
            if (!noFood.contains(i)) {
                foodNodes.add(i);
            }
        }

        List<Integer> currentPath = new ArrayList<>();
        currentPath.add(1);
        boolean[] vis = new boolean[11];
        vis[1] = true;
        
        dfsSearch(1, currentPath, vis);

        if (validCycles.isEmpty()) {
            System.out.println("No path found that covers all food nodes.");
            return;
        }

        List<Integer> bestCycle = null;
        int minNoFoodCount = Integer.MAX_VALUE;

        for (List<Integer> cycle : validCycles) {
            int noFoodCount = 0;
            for (int node : cycle) {
                if (node != 1 && noFood.contains(node)) {
                    noFoodCount++;
                }
            }
            if (noFoodCount < minNoFoodCount) {
                minNoFoodCount = noFoodCount;
                bestCycle = cycle;
            } else if (noFoodCount == minNoFoodCount) {
                if (bestCycle == null || cycle.size() < bestCycle.size()) {
                    bestCycle = cycle;
                }
            }
        }

        System.out.println("Path:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bestCycle.size(); i++) {
            sb.append(bestCycle.get(i));
            if (i < bestCycle.size() - 1) sb.append("-> ");
        }
        System.out.println(sb.toString());
    }

    private static void dfsSearch(int u, List<Integer> path, boolean[] vis) {
        if (path.size() >= 3) {
            if (adj.get(u).contains(1)) {
                boolean coversAll = true;
                for (int f : foodNodes) {
                    if (!path.contains(f)) {
                        coversAll = false;
                        break;
                    }
                }
                if (coversAll) {
                    List<Integer> cycle = new ArrayList<>(path);
                    cycle.add(1);
                    validCycles.add(cycle);
                }
            }
        }

        for (int v : adj.get(u)) {
            if (v == 1) continue;
            if (!vis[v]) {
                vis[v] = true;
                path.add(v);
                dfsSearch(v, path, vis);
                path.remove(path.size() - 1);
                vis[v] = false;
            }
        }
    }

    public static void solveHarvestingI(List<Officer> generals) {
        int bestFood = 0;
        List<Officer> bestTeam = null;
        String bestType = "";
        String bestLevel = "";

        int n = generals.size();
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    Officer o1 = generals.get(i);
                    Officer o2 = generals.get(j);
                    Officer o3 = generals.get(k);

                    int sumPol = o1.politic + o2.politic + o3.politic;
                    double multPol = 1.0;
                    String levelPol = "C";
                    if (sumPol >= 250) { multPol = 2.0; levelPol = "S"; }
                    else if (sumPol >= 220) { multPol = 1.5; levelPol = "A"; }
                    else if (sumPol >= 190) { multPol = 1.2; levelPol = "B"; }
                    else { multPol = 1.0; levelPol = "C"; }
                    int foodPol = (int) (1000 * multPol);

                    if (foodPol > bestFood) {
                        bestFood = foodPol;
                        List<Officer> team = new ArrayList<>();
                        team.add(o1);
                        team.add(o2);
                        team.add(o3);
                        bestTeam = team;
                        bestType = "Politic";
                        bestLevel = levelPol;
                    }

                    int sumInt = o1.intelligence + o2.intelligence + o3.intelligence;
                    double multInt = 0.8;
                    String levelInt = "C";
                    if (sumInt >= 250) { multInt = 1.8; levelInt = "S"; }
                    else if (sumInt >= 220) { multInt = 1.3; levelInt = "A"; }
                    else if (sumInt >= 190) { multInt = 1.0; levelInt = "B"; }
                    else { multInt = 0.8; levelInt = "C"; }
                    int foodInt = (int) (1000 * multInt);

                    if (foodInt > bestFood) {
                        bestFood = foodInt;
                        List<Officer> team = new ArrayList<>();
                        team.add(o1);
                        team.add(o2);
                        team.add(o3);
                        bestTeam = team;
                        bestType = "Intelligence";
                        bestLevel = levelInt;
                    }
                }
            }
        }

        System.out.println("\n--- Food Harvesting I (Max Production) ---");
        System.out.println("Max Food Production: " + bestFood);
        System.out.println("Buff Category: " + bestType + " (Level " + bestLevel + ")");
        System.out.println("Best Team Selected:");
        for (Officer o : bestTeam) {
            System.out.print(o);
        }
    }

    public static void solveHarvestingII() {
        int[] soldiers = {0, 0, 9, 8, 5, 3, 6, 8, 3, 5, 6}; // 1-indexed
        int[] strengths = {90, 70, 42}; // Xu Sheng, Lu Meng, Xiao Qiao
        String[] genNames = {"Xu Sheng", "Lu Meng", "Xiao Qiao"};

        int startState = 0; // (1,1,1,0)
        int targetState = 511; // (1,1,1,511)

        int[] dist = new int[512000];
        Arrays.fill(dist, -1);
        int[] parentState = new int[512000];
        int[] parentAction = new int[512000];

        Queue<Integer> q = new LinkedList<>();
        dist[startState] = 0;
        q.add(startState);

        while (!q.isEmpty()) {
            int curr = q.poll();
            if (curr == targetState) break;

            int temp = curr;
            int mask = temp % 512; temp /= 512;
            int u3 = (temp % 10) + 1; temp /= 10;
            int u2 = (temp % 10) + 1; temp /= 10;
            int u1 = temp + 1;

            int[] positions = {u1, u2, u3};

            for (int k = 0; k < 3; k++) {
                int u = positions[k];
                for (int v : adj.get(u)) {
                    int newMask = mask;
                    if (v >= 2 && v <= 10 && strengths[k] >= soldiers[v]) {
                        newMask = mask | (1 << (v - 2));
                    }
                    
                    int[] newPositions = positions.clone();
                    newPositions[k] = v;

                    int nextState = (newPositions[0] - 1) * 51200 + (newPositions[1] - 1) * 5120 + (newPositions[2] - 1) * 512 + newMask;
                    
                    if (dist[nextState] == -1) {
                        dist[nextState] = dist[curr] + 1;
                        parentState[nextState] = curr;
                        parentAction[nextState] = k * 100 + v;
                        q.add(nextState);
                    }
                }
            }
        }

        if (dist[targetState] == -1) {
            System.out.println("No solution found for Food Harvesting II.");
            return;
        }

        List<Integer> actions = new ArrayList<>();
        int curr = targetState;
        while (curr != startState) {
            actions.add(parentAction[curr]);
            curr = parentState[curr];
        }
        Collections.reverse(actions);

        List<List<Integer>> paths = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            List<Integer> singlePath = new ArrayList<>();
            singlePath.add(1);
            paths.add(singlePath);
        }

        for (int act : actions) {
            int genIdx = act / 100;
            int nextNode = act % 100;
            paths.get(genIdx).add(nextNode);
        }

        System.out.println("\n--- Food Harvesting II (Best Simulation) ---");
        System.out.println("Total cost = " + dist[targetState]);
        for (int i = 0; i < 3; i++) {
            System.out.println(genNames[i] + ":");
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < paths.get(i).size(); j++) {
                sb.append(paths.get(i).get(j));
                if (j < paths.get(i).size() - 1) sb.append("->");
            }
            System.out.println(sb.toString());
            System.out.println("Cost = " + (paths.get(i).size() - 1));
        }
    }
}
