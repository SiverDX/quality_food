package de.cadentem.quality_food.core;

public enum Rarity {
    NONE,
    IRON,
    GOLD,
    DIAMOND;

    public static Rarity get(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return NONE;
        }

        return values()[ordinal];
    }
}
