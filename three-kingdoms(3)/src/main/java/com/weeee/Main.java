package com.weeee;

import java.util.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main {
    public static void main(String[] args) {
        OfficerData.initData();

        Officer emperor = OfficerData.getEmperor();
        Officer chiefOfMilitary = OfficerData.getChiefOfMilitary();
        Officer chiefOfManagement = OfficerData.getChiefOfManagement();
        List<Officer> generals = OfficerData.getGenerals();

        KingdomTree tree = new KingdomTree(emperor, chiefOfMilitary, chiefOfManagement, generals);
        EnemyGraph.build();
        FoodDFS.build();

        
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== Three Kingdoms: Battle of Red Cliff =====");
            System.out.println("1. Kingdom Hierarchy Tree");
            System.out.println("2. Sort & Search officers");
            System.out.println("3. Borrow Arrows");
            System.out.println("4. Enemy Fortress Path");
            System.out.println("5. Food Collection");
            System.out.println("6. Decrypt Message");
            System.out.println("7. Fire Clusters");
            System.out.println("8. Escape Hua Rong Road");
            System.out.println("9. Launch Graphical User Interface (GUI)");
            System.out.println("10. Exit");
            System.out.print("Choose: ");
            int c = sc.nextInt();
            sc.nextLine();
            switch (c) {
                case 1:
                    tree.printTree();
                    break;
                case 2: {
                    boolean back = false;
                    while (!back) {
                        System.out.println("\n--- General Arrangement (Sort & Search) ---");
                        System.out.println("1. Sort Generals");
                        System.out.println("2. Binary Search General");
                        System.out.println("3. Suggest Team");
                        System.out.println("4. Back to Main Menu");
                        System.out.print("Choose: ");
                        int choice2 = sc.nextInt();
                        sc.nextLine();
                        switch (choice2) {
                            case 1: {
                                System.out.print("Enter attribute (strength/leadership/intelligence/politic/hitpoint): ");
                                String attr = sc.nextLine().trim();
                                System.out.print("Enter order (asc/desc): ");
                                String order = sc.nextLine().trim();
                                boolean asc = order.equalsIgnoreCase("asc");
                                List<Officer> copy = new ArrayList<>(generals);
                                SortSearch.sortByAttribute(copy, attr, asc);
                                System.out.println("\nSorted Generals by " + attr + " (" + (asc ? "Ascending" : "Descending") + "):");
                                for (Officer o : copy) {
                                    System.out.print(o);
                                }
                                break;
                            }
                            case 2: {
                                System.out.print("Enter attribute to search (strength/leadership/intelligence/politic/hitpoint): ");
                                String attr = sc.nextLine().trim();
                                System.out.print("Enter target value: ");
                                int targetVal = sc.nextInt();
                                sc.nextLine();
                                List<Officer> copy = new ArrayList<>(generals);
                                Officer found = SortSearch.binarySearch(copy, targetVal, attr);
                                if (found != null) {
                                    System.out.println("\nFound General: " + found);
                                } else {
                                    System.out.println("\nNo General found with " + attr + " = " + targetVal);
                                }
                                break;
                            }
                            case 3: {
                                System.out.print("Enter team field (strength/leadership/intelligence/politic): ");
                                String field = sc.nextLine().trim();
                                System.out.print("Enter level (S/A/B/C): ");
                                String level = sc.nextLine().trim();
                                List<Officer> copy = new ArrayList<>(generals);
                                SortSearch.suggestTeam(copy, field, level);
                                break;
                            }
                            case 4:
                                back = true;
                                break;
                            default:
                                System.out.println("Invalid option.");
                                break;
                        }
                    }
                    break;
                }
                case 3: {
                    System.out.println("\n--- Borrowing Arrows with Straw Boats ---");
                    System.out.println("1. Run Default Simulation");
                    System.out.println("2. Run Custom Simulation");
                    System.out.println("3. Run Dynamic Simulation (DP)");
                    System.out.print("Choose: ");
                    int choice3 = sc.nextInt();
                    sc.nextLine();
                    if (choice3 == 1) {
                        ArrowBoat.simulate();
                    } else if (choice3 == 2) {
                        System.out.println("Enter straw men count for each side:");
                        System.out.print("Front: ");
                        int f = sc.nextInt();
                        System.out.print("Left: ");
                        int l = sc.nextInt();
                        System.out.print("Right: ");
                        int r = sc.nextInt();
                        System.out.print("Back: ");
                        int b = sc.nextInt();
                        System.out.print("Enter number of arrow waves: ");
                        int numWaves = sc.nextInt();
                        int[] waves = new int[numWaves];
                        System.out.println("Enter arrows count for each wave:");
                        for (int i = 0; i < numWaves; i++) {
                            System.out.print("Wave " + (i + 1) + ": ");
                            waves[i] = sc.nextInt();
                        }
                        sc.nextLine();
                        ArrowBoat.simulate(f, l, r, b, waves);
                    } else if (choice3 == 3) {
                        System.out.println("Running Dynamic Simulation (DP)...");
                        int[] defaultRandomWaves = {300, 1500, 1000, 2000, 600, 800, 300, 500, 400};
                        ArrowBoat.simulateDynamic(10, 50, 50, 15, defaultRandomWaves);
                    } else {
                        System.out.println("Invalid choice.");
                    }
                    break;
                }
                case 4: {
                    System.out.println("\n--- Enemy Fortress Path ---");
                    System.out.println("1. BFS (Shortest Steps)");
                    System.out.println("2. Dijkstra (Shortest Time)");
                    System.out.println("3. A* Search (Shortest Time)");
                    System.out.println("4. Best First Search (Greedy Distance)");
                    System.out.print("Choose: ");
                    int choice4 = sc.nextInt();
                    System.out.print("Enter the base camp for the enemy base camp: (2-10): ");
                    int target = sc.nextInt();
                    sc.nextLine();
                    if (choice4 == 1) {
                        EnemyGraph.bfs(target);
                    } else if (choice4 == 2 || choice4 == 3) {
                        System.out.println("Select Officer/General:");
                        List<Officer> allO = new ArrayList<>(generals);
                        allO.add(emperor);
                        allO.add(chiefOfMilitary);
                        allO.add(chiefOfManagement);
                        for (int i = 0; i < allO.size(); i++) {
                            System.out.println((i + 1) + ". " + allO.get(i).name + " (" + allO.get(i).type + ")");
                        }
                        System.out.print("Choose officer: ");
                        int oChoice = sc.nextInt();
                        sc.nextLine();
                        if (oChoice >= 1 && oChoice <= allO.size()) {
                            if (choice4 == 2) {
                                EnemyGraph.dijkstra(allO.get(oChoice - 1), target);
                            } else {
                                EnemyGraph.aStar(allO.get(oChoice - 1), target);
                            }
                        } else {
                            System.out.println("Invalid choice.");
                        }
                    } else if (choice4 == 4) {
                        EnemyGraph.bestFirstSearch(target);
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;
                }
                case 5: {
                    System.out.println("\n--- Food Harvesting ---");
                    System.out.println("1. Food Harvesting Hamiltonian Cycle");
                    System.out.println("2. Food Harvesting I (Max Production)");
                    System.out.println("3. Food Harvesting II (Best Simulation)");
                    System.out.print("Choose: ");
                    int choice5 = sc.nextInt();
                    sc.nextLine();
                    if (choice5 == 1) {
                        System.out.print("Enter node without food (separated by space, e.g. 9): ");
                        String line = sc.nextLine().trim();
                        Set<Integer> noFood = new HashSet<>();
                        if (!line.isEmpty()) {
                            String[] tokens = line.split("\\s+");
                            for (String t : tokens) {
                                try {
                                    noFood.add(Integer.parseInt(t));
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                        FoodDFS.findPath(noFood);
                    } else if (choice5 == 2) {
                        FoodDFS.solveHarvestingI(generals);
                    } else if (choice5 == 3) {
                        FoodDFS.solveHarvestingII();
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;
                }
                case 6: {
                    System.out.println("\n--- Caesar Cipher (Decryption & Encryption) ---");
                    System.out.println("1. Decrypt Default Message");
                    System.out.println("2. Custom Encryption");
                    System.out.println("3. Custom Decryption");
                    System.out.println("4. Custom Encryption (XOR cipher)");
                    System.out.println("5. Custom Decryption (XOR cipher)");
                    System.out.println("6. Custom Encryption (Secure &num{ ciphers })");
                    System.out.println("7. Custom Decryption (Secure &num{ ciphers })");
                    System.out.print("Choose: ");
                    int choice6 = sc.nextInt();
                    sc.nextLine();
                    switch (choice6) {
                        case 1: {
                            String defaultMsg = "^hkcpzl$^jhv$^jhv$av$bzl$^aol$^johpu$^zayhalnlt,$(ojpod)$pz$av$johpu$opz$(zwpozlsaahi)$dpao$zayvun$pyvu$johpuz.";
                            System.out.println("Ciphertext: " + defaultMsg);
                            System.out.println("Shift: 7");
                            System.out.println("Decrypted: " + CaesarCipher.decrypt(defaultMsg, 7));
                            break;
                        }
                        case 2: {
                            System.out.print("Enter plaintext: ");
                            String plain = sc.nextLine();
                            System.out.print("Enter shift key: ");
                            int shift = sc.nextInt();
                            sc.nextLine();
                            System.out.println("Encrypted: " + CaesarCipher.encrypt(plain, shift));
                            break;
                        }
                        case 3: {
                            System.out.print("Enter ciphertext: ");
                            String cipher = sc.nextLine();
                            System.out.print("Enter shift key: ");
                            int shift = sc.nextInt();
                            sc.nextLine();
                            System.out.println("Decrypted: " + CaesarCipher.decrypt(cipher, shift));
                            break;
                        }
                        case 4: {
                            System.out.print("Enter plaintext: ");
                            String plain = sc.nextLine();
                            System.out.print("Enter XOR key: ");
                            String key = sc.nextLine();
                            System.out.println("Encrypted: " + CaesarCipher.encrypt2(plain, key));
                            break;
                        }
                        case 5: {
                            System.out.print("Enter ciphertext: ");
                            String cipher = sc.nextLine();
                            System.out.print("Enter XOR key: ");
                            String key = sc.nextLine();
                            System.out.println("Decrypted: " + CaesarCipher.decrypt2(cipher, key));
                            break;
                        }
                        case 6: {
                            System.out.print("Enter plaintext: ");
                            String plain = sc.nextLine();
                            System.out.print("Enter shift key: ");
                            int shift = sc.nextInt();
                            System.out.print("Enter num value: ");
                            int num = sc.nextInt();
                            sc.nextLine();
                            System.out.println("Encrypted: " + CaesarCipher.encrypt(plain, shift, num));
                            break;
                        }
                        case 7: {
                            System.out.print("Enter ciphertext (e.g. &7{...}): ");
                            String cipher = sc.nextLine();
                            System.out.print("Enter shift key: ");
                            int shift = sc.nextInt();
                            sc.nextLine();
                            System.out.println("Decrypted: " + CaesarCipher.decrypt(cipher, shift));
                            break;
                        }
                        default:
                            System.out.println("Invalid option.");
                            break;
                    }
                    break;
                }
                case 7: {
                    System.out.println("\n--- Fire Clusters ---");
                    System.out.println("1. Count Clusters (Basic)");
                    System.out.println("2. Find Optimized Fireball Coordinates (Extra)");
                    System.out.print("Choose: ");
                    int choice7 = sc.nextInt();
                    sc.nextLine();
                    if (choice7 == 1) {
                        FireCluster.count(sc);
                    } else if (choice7 == 2) {
                        FireCluster.findOptimizedPoints(sc);
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;
                }
                case 8:
                    HuaRongMaze.bfs();
                    break;
                case 9: {
                    System.out.println("Launching JavaFX Graphical User Interface (GUI)...");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Application.launch(App.class);
                            } catch (IllegalStateException ex) {
                                javafx.application.Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Stage newStage = new Stage();
                                        try {
                                            new App().start(newStage);
                                        } catch (Exception e2) {
                                            e2.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }).start();
                    break;
                }
                case 10: {
                    sc.close();
                    System.exit(0);
                }
                default: {
                    sc.close();
                    System.exit(0);
                }
            }
        }
    }
}