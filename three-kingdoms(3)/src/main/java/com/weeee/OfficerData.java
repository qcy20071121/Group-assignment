package com.weeee;

import java.util.Arrays;
import java.util.List;

public class OfficerData {
    private static List<Officer> generals;
    private static Officer emperor;
    private static Officer chiefOfMilitary;
    private static Officer chiefOfManagement;

    public static void initData() {
        generals = Arrays.asList(
            new Officer("Xu Sheng", "Archer", 90, 78, 72, 40, 94),
            new Officer("Zhu Ge Jin", "Archer", 63, 61, 88, 82, 71),
            new Officer("Lu Su", "Infantry", 43, 87, 84, 88, 53),
            new Officer("Tai Shi Ci", "Cavalry", 96, 81, 43, 33, 97),
            new Officer("Xiao Qiao", "Infantry", 42, 52, 89, 77, 34),
            new Officer("Da Qiao", "Cavalry", 39, 62, 90, 62, 41),
            new Officer("Zhou Tai", "Infantry", 92, 89, 72, 43, 99),
            new Officer("Gan Ning", "Archer", 98, 92, 45, 23, 97),
            new Officer("Lu Meng", "Cavalry", 70, 77, 93, 83, 88),
            new Officer("Huang Gai", "Infantry", 83, 98, 72, 42, 89)
        );

        emperor = new Officer("Sun Quan", "King", 96, 98, 72, 77, 95);
        chiefOfMilitary = new Officer("Zhou Yu", "Military", 80, 86, 97, 80, 90);
        chiefOfManagement = new Officer("Zhang Zhao", "Management", 22, 80, 89, 99, 60);
    }

    public static List<Officer> getGenerals() {
        return generals;
    }

    public static Officer getEmperor() {
        return emperor;
    }

    public static Officer getChiefOfMilitary() {
        return chiefOfMilitary;
    }

    public static Officer getChiefOfManagement() {
        return chiefOfManagement;
    }
}
