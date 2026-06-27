package com.weeee;

import java.util.*;

public class SortSearch {

    public static void sortByAttribute(List<Officer> list, String attr) {
        sortByAttribute(list, attr, true);
    }

    public static void sortByAttribute(List<Officer> list, String attr, boolean ascending) {
        if (list == null || list.size() <= 1) return;
        List<Officer> temp = new ArrayList<>(list);
        mergeSort(list, temp, 0, list.size() - 1, attr, ascending);
    }

    private static void mergeSort(List<Officer> list, List<Officer> temp, int left, int right, String attr, boolean ascending) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(list, temp, left, mid, attr, ascending);
        mergeSort(list, temp, mid + 1, right, attr, ascending);
        merge(list, temp, left, mid, right, attr, ascending);
    }

    private static void merge(List<Officer> list, List<Officer> temp, int left, int mid, int right, String attr, boolean ascending) {
        for (int i = left; i <= right; i++) {
            temp.set(i, list.get(i));
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            int valI = getAbility(temp.get(i), attr);
            int valJ = getAbility(temp.get(j), attr);
            boolean compare = ascending ? (valI <= valJ) : (valI >= valJ);
            if (compare) {
                list.set(k++, temp.get(i++));
            } else {
                list.set(k++, temp.get(j++));
            }
        }

        while (i <= mid) {
            list.set(k++, temp.get(i++));
        }
        while (j <= right) {
            list.set(k++, temp.get(j++));
        }
    }

    public static Officer binarySearch(List<Officer> list, int target, String attr) {
        // Binary search requires the list to be sorted by the same attribute.
        sortByAttribute(list, attr);

        int l = 0;
        int r = list.size() - 1;

        while (l <= r) {
            int m = l + (r - l) / 2;
            int val = getAbility(list.get(m), attr);

            if (val == target) {
                return list.get(m);
            } else if (val < target) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }

        return null;
    }

    public static void suggestTeam(List<Officer> list, String field, String level) {
        if (list.size() < 3) {
            System.out.println("Not enough generals to form a team.");
            return;
        }

        if (!isTeamField(field)) {
            System.out.println("Invalid team field. Use politic, leadership, strength, or intelligence.");
            return;
        }

        // Sort by the selected field before using binary search.
        sortByAttribute(list, field);

        int minSum = getMinSum(level);
        int maxSum = getMaxSum(level);

        for (int i = 0; i < list.size() - 2; i++) {
            for (int j = i + 1; j < list.size() - 1; j++) {
                Officer first = list.get(i);
                Officer second = list.get(j);

                int firstVal = getAbility(first, field);
                int secondVal = getAbility(second, field);

                int currentSum = firstVal + secondVal;

                int neededMin = minSum - currentSum;
                // int neededMax = maxSum - currentSum;

                int k = lowerBound(list, neededMin, field);

                // Make sure the third general is not the same as first or second.
                while (k < list.size() && (k == i || k == j)) {
                    k++;
                }

                if (k < list.size()) {
                    Officer third = list.get(k);
                    int thirdVal = getAbility(third, field);
                    int total = currentSum + thirdVal;

                    if (total >= minSum && total <= maxSum) {
                        System.out.println("Team " + level + " (" + field + "):");
                        System.out.println(first);
                        System.out.println(second);
                        System.out.println(third);
                        System.out.println("Total " + field + " ability = " + total);
                        return;
                    }
                }
            }
        }

        System.out.println("No suitable " + level + " level team found for " + field + ".");
    }

    private static int getAbility(Officer o, String attr) {
        String key = attr.toLowerCase();
        switch (key) {
            case "strength":
                return o.strength;
            case "leadership":
                return o.leadership;
            case "intelligence":
                return o.intelligence;
            case "politic":
            case "political":
                return o.politic;
            case "hp":
            case "hitpoint":
            case "hit point":
                return o.hitPoint;
            default:
                throw new IllegalArgumentException("Invalid attribute: " + attr);
        }
    }

    private static boolean isTeamField(String field) {
        String f = field.toLowerCase();
        return f.equals("politic")
                || f.equals("political")
                || f.equals("leadership")
                || f.equals("strength")
                || f.equals("intelligence");
    }

    private static int getMinSum(String level) {
        String lvl = level.toUpperCase();
        switch (lvl) {
            case "S":
                return 250;
            case "A":
                return 220;
            case "B":
                return 190;
            case "C":
                return 0;
            default:
                throw new IllegalArgumentException("Invalid level: " + level);
        }
    }

    private static int getMaxSum(String level) {
        String lvl = level.toUpperCase();
        switch (lvl) {
            case "S":
                return Integer.MAX_VALUE;
            case "A":
                return 249;
            case "B":
                return 219;
            case "C":
                return 190;
            default:
                throw new IllegalArgumentException("Invalid level: " + level);
        }
    }

    private static int lowerBound(List<Officer> list, int target, String attr) {
        int l = 0;
        int r = list.size();

        while (l < r) {
            int m = l + (r - l) / 2;
            int val = getAbility(list.get(m), attr);

            if (val < target) {
                l = m + 1;
            } else {
                r = m;
            }
        }

        return l;
    }
}