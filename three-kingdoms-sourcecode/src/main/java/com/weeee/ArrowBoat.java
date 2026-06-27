package com.weeee;

import java.util.*;

public class ArrowBoat {
    public static void simulate() {
        simulate(10, 50, 50, 15, new int[]{2000, 1500, 1000, 800, 600, 500, 300, 300});
    }

    public static void simulate(int front, int left, int right, int back, int[] waves) {
        String[] directions = {"left", "right", "back", "front"};
        int[] straw = {left, right, back, front};
        int[] usage = {0, 0, 0, 0};

        double[] eff = {1.0, 0.8, 0.4, 0.0};
        List<String> dirs = new ArrayList<>();
        List<Integer> received = new ArrayList<>();
        int total = 0;

        for (int i = 0; i < waves.length; i++) {
            int arrowCount = waves[i];
            int best = -1;
            int bestIdx = -1;

            for (int d = 0; d < 4; d++) {
                int strawCount = straw[d];
                int useCount = usage[d];
                double efficiency;
                if (useCount < 3) {
                    efficiency = eff[useCount];
                } else {
                    efficiency = 0.0;
                }
                int val = (int) (arrowCount * strawCount * efficiency / 100);

                if (val > best) {
                    best = val;
                    bestIdx = d;
                }
            }

            usage[bestIdx] = usage[bestIdx] + 1;
            
            dirs.add(directions[bestIdx]);
            received.add(best);
            total += best;
        }

        System.out.println("\n--- Arrow Borrowing Simulation Results ---");
        System.out.println("Boat direction: " + dirs);
        System.out.println("Arrow received: " + received);
        System.out.println("Total = " + total);
    }

    public static void simulateDynamic(int front, int left, int right, int back, int[] waves) {
        double[] eff = {1.0, 0.8}; // Only 2 uses

        int n = waves.length;
        Integer[][][][][] memo = new Integer[n][3][3][3][3];
        String[][][][][] bestChoice = new String[n][3][3][3][3];

        solve(0, 0, 0, 0, 0, waves, left, right, back, front, eff, memo, bestChoice);

        List<String> dirs = new ArrayList<>();
        List<Integer> received = new ArrayList<>();
        int total = 0;

        int l = 0, r = 0, b = 0, f = 0;
        for (int i = 0; i < n; i++) {
            String choice = bestChoice[i][l][r][b][f];
            dirs.add(choice);
            int arrowCount = waves[i];
            int obtained = 0;
            if (choice.equals("left")) {
                obtained = (int) (left * eff[l] * arrowCount / 100);
                l++;
            } else if (choice.equals("right")) {
                obtained = (int) (right * eff[r] * arrowCount / 100);
                r++;
            } else if (choice.equals("back")) {
                obtained = (int) (back * eff[b] * arrowCount / 100);
                b++;
            } else if (choice.equals("front")) {
                obtained = (int) (front * eff[f] * arrowCount / 100);
                f++;
            } else {
                obtained = 0;
            }
            received.add(obtained);
            total += obtained;
        }

        System.out.println("\n--- Dynamic Arrow Borrowing Results (DP) ---");
        System.out.println("Boat direction: " + dirs);
        System.out.println("Arrow received: " + received);
        System.out.println("Total = " + total);
    }

    private static int solve(int i, int l, int r, int b, int f, int[] waves, 
                                 int left, int right, int back, int front, double[] eff, 
                                 Integer[][][][][] memo, String[][][][][] bestChoice) {
        if (i == waves.length) return 0;
        if (memo[i][l][r][b][f] != null) return memo[i][l][r][b][f];

        int maxVal = -1;
        String choice = "";

        // Skip
        int valSkip = solve(i + 1, l, r, b, f, waves, left, right, back, front, eff, memo, bestChoice);
        if (valSkip > maxVal) {
            maxVal = valSkip;
            choice = "skip";
        }
        // Front
        if (f < 2) {
            int valF = (int) (front * eff[f] * waves[i] / 100) 
                        + solve(i + 1, l, r, b, f + 1, waves, left, right, back, front, eff, memo, bestChoice);
            if (valF >= maxVal) {
                maxVal = valF;
                choice = "front";
            }
        }
        // Back
        if (b < 2) {
            int valB = (int) (back * eff[b] * waves[i] / 100)
                        + solve(i + 1, l, r, b + 1, f, waves, left, right, back, front, eff, memo, bestChoice);
            if (valB >= maxVal) {
                maxVal = valB;
                choice = "back";
            }
        }
        // Right
        if (r < 2) {
            int valR = (int) (right * eff[r] * waves[i] / 100)
                        + solve(i + 1, l, r + 1, b, f, waves, left, right, back, front, eff, memo, bestChoice);
            if (valR >= maxVal) {
                maxVal = valR;
                choice = "right";
            }
        }
        // Left
        if (l < 2) {
            int valL = (int) (left * eff[l] * waves[i] / 100)
                        + solve(i + 1, l + 1, r, b, f, waves, left, right, back, front, eff, memo, bestChoice);
            if (valL >= maxVal) {
                maxVal = valL;
                choice = "left";
            }
        }

        bestChoice[i][l][r][b][f] = choice;
        memo[i][l][r][b][f] = maxVal;
        return maxVal;
    }
}
