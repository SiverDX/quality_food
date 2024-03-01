package de.cadentem.quality_food.core;

import org.jetbrains.annotations.NotNull;

public enum Quality {
    NONE,
    NONE_PLAYER_PLACED,
    IRON,
    GOLD,
    DIAMOND;

    /** Returns the ordinal acting like the enum value {@link Quality#NONE_PLAYER_PLACED} does not exist */
    public static @NotNull Quality get(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return NONE;
        }

        return switch (ordinal) {
            case 1 -> IRON;
            case 2 -> GOLD;
            case 3 -> DIAMOND;
            default -> NONE;
        };
    }

    /** Returns the actual value related to the ordinal */
    public static @NotNull Quality getRaw(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return NONE;
        }

        return values()[ordinal];
    }

    /** Returns the ordinal acting like the enum value {@link Quality#NONE_PLAYER_PLACED} does not exist */
    public int value() {
        return switch (this) {
            case IRON -> 1;
            case GOLD -> 2;
            case DIAMOND -> 3;
            default -> 0;
        };
    }
}
