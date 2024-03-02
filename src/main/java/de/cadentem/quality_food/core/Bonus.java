package de.cadentem.quality_food.core;

public record Bonus(Type type, float amount) {
    public static final Bonus DEFAULT = additive(0);

    public static Bonus additive(float amount) {
        return new Bonus(Type.ADDITIVE, amount);
    }

    public static Bonus multiplicative(float amount) {
        return new Bonus(Type.MULTIPLICATIVE, amount);
    }

    public enum Type {
        ADDITIVE,
        MULTIPLICATIVE
    }
}
