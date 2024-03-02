package de.cadentem.quality_food.core;

import org.jetbrains.annotations.NotNull;

public enum Quality {
    NONE,
    IRON,
    GOLD,
    DIAMOND,
    UNDEFINED, // For potential later use
    NONE_PLAYER_PLACED;

    /** Returns an implemented quality for the ordinal */
    public static @NotNull Quality get(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return NONE;
        }

        return switch (ordinal) {
            case 1 -> IRON;
            case 2 -> GOLD;
            case 3, 4 -> DIAMOND;
            default -> NONE;
        };
    }

    public int level() {
        return switch (this) {
            case IRON -> 1;
            case GOLD -> 2;
            case DIAMOND -> 3;
            default -> 0;
        };
    }

    public String getName() {
        return switch (this) {
            case IRON -> "Iron";
            case GOLD -> "Gold";
            case DIAMOND, UNDEFINED -> "Diamond";
            default -> "";
        };
    }
}
