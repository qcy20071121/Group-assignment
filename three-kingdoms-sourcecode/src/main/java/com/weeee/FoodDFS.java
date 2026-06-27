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
        solveHarvestingII(new Scanner(System.in));
    }

    public static void solveHarvestingII(Scanner scanner) {
    // ---------- Get all generals from data ----------
    List<Officer> allGenerals = OfficerData.getGenerals();
    if (allGenerals.isEmpty()) {
        System.out.println("No general data available!");
        return;

    }

    // Display available generals with their strength
    System.out.println("Available generals:");
    for (int i = 0; i < allGenerals.size(); i++) {
        Officer o = allGenerals.get(i);
        System.out.printf("%d: %s (Strength %d)\n", i + 1, o.name, o.strength);
    }
  
    // User input for three generals (1‑based indices)
    int[] chosenIndices = new int[3];
    System.out.println("Please select three generals (enter numbers separated by spaces, e.g. 1 2 3):");
    for (int i = 0; i < 3; i++) {
        while (true) {
            System.out.printf("General #%d: ", i + 1);
            int input = scanner.nextInt();
            if (input >= 1 && input <= allGenerals.size()) {
                // Check duplicate selection
                boolean duplicate = false;
                for (int j = 0; j < i; j++) {
                    if (chosenIndices[j] == input) {
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    chosenIndices[i] = input;
                    break;
                } else {
                    System.out.println("This general is already selected. Please choose another.");
                }
            } else {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    // Build selected arrays
    int[] strengths = new int[3];
    String[] genNames = new String[3];
    for (int i = 0; i < 3; i++) {
        Officer selected = allGenerals.get(chosenIndices[i] - 1);
        strengths[i] = selected.strength;
        genNames[i] = selected.name;
    }

    System.out.print("Enter enemy soldier multiplier for demonstration (1-10, default 1): ");
    String multInput = scanner.next().trim();
    int multiplier = 1;
    try {
        if (!multInput.isEmpty()) {
            multiplier = Integer.parseInt(multInput);
        }
    } catch (NumberFormatException e) {
        multiplier = 1;
    }

    int[] soldiers = {0, 0, 9, 8, 5, 3, 6, 8, 3, 5, 6}; // 1-indexed
    if (multiplier > 1) {
        for (int i = 0; i < soldiers.length; i++) {
            soldiers[i] *= multiplier;
        }
        System.out.println("[Demonstration Mode] Enemy soldier counts are multiplied by " + multiplier + "!");
    }

    // state = u1_idx*6553600 + u2_idx*655360 + u3_idx*65536 + c1*16384 + c2*4096 + started1*2048 + started2*1024 + started3*512 + mask
    int startState = 0; // all at 1, started=0,0,0, c1=c2=0, mask=0
    int targetState = 65535; // all at 1, started=1,1,1, c1=c2=3, mask=511

    int[] dist = new int[65536000];
    Arrays.fill(dist, -1);
    int[] parentState = new int[65536000];
    int[] parentAction = new int[65536000];

    Queue<Integer> q = new LinkedList<>();
    dist[startState] = 0;
    q.add(startState);

    while (!q.isEmpty()) {
        int curr = q.poll();
        if (curr == targetState) break;

        int mask = curr % 512;
        int t2 = curr / 512;
        int started3 = t2 % 2;
        int started2 = (t2 / 2) % 2;
        int started1 = (t2 / 4) % 2;
        int t3 = t2 / 8;
        int c2 = t3 % 4;
        int c1 = (t3 / 4) % 4;
        int t4 = t3 / 16;
        int u3 = (t4 % 10) + 1;
        int u2 = ((t4 / 10) % 10) + 1;
        int u1 = (t4 / 100) + 1;

        int totalCaptured = Integer.bitCount(mask);
        int c3 = totalCaptured - c1 - c2;

        int[] positions = {u1, u2, u3};
        int[] started = {started1, started2, started3};
        int[] c = {c1, c2, c3};

        for (int k = 0; k < 3; k++) {
            int u = positions[k];
            for (int v : adj.get(u)) {
                int newMask = mask;
                int[] newC = c.clone();
                boolean validCapture = true;

                if (v >= 2 && v <= 10 && (mask & (1 << (v - 2))) == 0) {
                    if (strengths[k] >= soldiers[v]) {
                        newMask = mask | (1 << (v - 2));
                        newC[k] = c[k] + 1;
                        if (newC[k] > 3) {
                            validCapture = false; // each general captures at most 3
                        }
                    }
                }

                if (!validCapture) continue;

                int[] newPositions = positions.clone();
                newPositions[k] = v;

                int[] newStarted = started.clone();
                if (v != 1) {
                    newStarted[k] = 1;
                }

                int nextState = (newPositions[0] - 1) * 6553600 + 
                                (newPositions[1] - 1) * 655360 + 
                                (newPositions[2] - 1) * 65536 + 
                                newC[0] * 16384 + 
                                newC[1] * 4096 + 
                                newStarted[0] * 2048 + 
                                newStarted[1] * 1024 + 
                                newStarted[2] * 512 + 
                                newMask;

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

    boolean[] captured = new boolean[11];
    int[] genFoodCount = new int[3];
    for (int act : actions) {
        int genIdx = act / 100;
        int nextNode = act % 100;
        paths.get(genIdx).add(nextNode);
        if (nextNode >= 2 && nextNode <= 10 && !captured[nextNode]) {
            if (strengths[genIdx] >= soldiers[nextNode]) {
                captured[nextNode] = true;
                genFoodCount[genIdx]++;
            }
        }
    }

    int finalMask = targetState % 512;
    int foodCampsHarvested = Integer.bitCount(finalMask);
    int totalFood = foodCampsHarvested * 100;

    System.out.println("\n--- Food Harvesting II (Best Simulation) ---");
    System.out.println("Total cost = " + dist[targetState]);
    System.out.println("Total food harvested = " + totalFood);
    for (int i = 0; i < 3; i++) {
        System.out.println(genNames[i] + ":");
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < paths.get(i).size(); j++) {
            sb.append(paths.get(i).get(j));
            if (j < paths.get(i).size() - 1) sb.append("->");
        }
        System.out.println(sb.toString());
        System.out.println("Cost = " + (paths.get(i).size() - 1));
        System.out.println("Food harvested = " + (genFoodCount[i] * 100));
    }
}
}
