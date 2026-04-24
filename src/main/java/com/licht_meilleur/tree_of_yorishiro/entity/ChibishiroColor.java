package com.licht_meilleur.tree_of_yorishiro.entity;

public enum ChibishiroColor {
    RED("red"),
    BLUE("blue"),
    YELLOW("yellow"),
    PURPLE("purple"),
    WHITE("white");

    private final String id;

    ChibishiroColor(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ChibishiroColor byIndex(int i) {
        ChibishiroColor[] values = values();
        if (i < 0 || i >= values.length) return RED;
        return values[i];
    }
}