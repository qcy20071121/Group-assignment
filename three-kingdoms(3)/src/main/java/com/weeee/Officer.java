package com.weeee;

public class Officer {
    String name;
    String type;
    int strength, leadership, intelligence, politic, hitPoint;

    public Officer(String name, String type, int strength, int leadership, int intelligence, int politic, int hitPoint) {
        this.name = name;
        this.type = type;
        this.strength = strength;
        this.leadership = leadership;
        this.intelligence = intelligence;
        this.politic = politic;
        this.hitPoint = hitPoint;
    }

    public int sum() {
        return strength + leadership + intelligence + politic + hitPoint;
    }

    @Override
    public String toString() {
        return name + " | STR:" + strength + " LDR:" + leadership + " INT:" + intelligence + " POL:" + politic + " HP:" + hitPoint + "\n";
    }
}