package com.weeee;

import java.util.ArrayList;
import java.util.List;

class TreeNode {
    Officer officer;
    List<TreeNode> children;

    public TreeNode(Officer officer) {
        this.officer = officer;
        children = new ArrayList<>();
    }

    public void addChild(Officer officer) {
        children.add(new TreeNode(officer));
    }

    public void addChild(TreeNode officer) {
        children.add(officer);
    }
}

public class KingdomTree {
    private TreeNode root;
    private TreeNode chiefOfMilitary;
    private TreeNode chiefOfManagement;

    public KingdomTree(Officer emperor, Officer chiefOfMilitary, Officer chiefOfManagement, List<Officer> generals) {

        root = new TreeNode(emperor);
        this.chiefOfMilitary = new TreeNode(chiefOfMilitary);
        this.chiefOfManagement = new TreeNode(chiefOfManagement);
        root.addChild(this.chiefOfMilitary);
        root.addChild(this.chiefOfManagement);

        for (Officer general : generals) {
            if (general.strength > general.intelligence) {
                this.chiefOfMilitary.addChild(general);
            } else {
                this.chiefOfManagement.addChild(general);
            }
        }
    }

    public void printTree() {
        System.out.println("\n===== Wu Kingdom Hierarchy Tree =====");
        if (root != null) {
            String roleStr = " [Emperor]";
            System.out.println(root.officer.name + roleStr + " (STR:" + root.officer.strength + ", INT:" + root.officer.intelligence + ")");
            for (int i = 0; i < root.children.size(); i++) {
                print(root.children.get(i), "", i == root.children.size() - 1);
            }
        }
    }

    private void print(TreeNode node, String prefix, boolean isLast) {
        if (node == null) return;
        
        System.out.print(prefix);
        System.out.print(isLast ? "└── " : "├── ");
        
        String roleStr = "";
        if (node.officer.name.equals("Zhou Yu")) {
            roleStr = " [Chief of Military]";
        } else if (node.officer.name.equals("Zhang Zhao")) {
            roleStr = " [Chief of Management]";
        } else {
            roleStr = " [General - " + node.officer.type + "]";
        }
        
        System.out.println(node.officer.name + roleStr + " (STR:" + node.officer.strength + ", INT:" + node.officer.intelligence + ")");

        String childPrefix = prefix + (isLast ? "    " : "│   ");
        for (int i = 0; i < node.children.size(); i++) {
            print(node.children.get(i), childPrefix, i == node.children.size() - 1);
        }
    }
}