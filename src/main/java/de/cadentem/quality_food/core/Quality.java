package de.cadentem.quality_food.core;

public enum Quality {
    NONE,
    IRON,
    GOLD,
    DIAMOND;

    public static Quality get(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return NONE;
        }

        return values()[ordinal];
    }
}
