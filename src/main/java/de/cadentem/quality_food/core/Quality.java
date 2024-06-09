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
        return get(ordinal, false);
    }

    /**
     * @param ordinal Ordinal of the stored quality
     * @param wasHarvested Whether the quality comes from a block or not
     * @return Usable quality object
     */
    public static @NotNull Quality get(int ordinal, boolean wasHarvested) {
        if (ordinal < 0 || ordinal >= values().length) {
            return NONE;
        }

        if (wasHarvested && ordinal == NONE_PLAYER_PLACED.ordinal()) {
            return NONE_PLAYER_PLACED;
        }

        return switch (ordinal) {
            case 1 -> IRON;
            case 2 -> GOLD;
            case 3, 4 -> DIAMOND;
            default -> NONE;
        };
    }

    public static @NotNull Quality byName(final String name) {
        return switch (name) {
            case "Iron" -> IRON;
            case "Gold" -> GOLD;
            case "Diamond" -> DIAMOND;
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
