package de.cadentem.quality_food.capability;

import net.minecraft.nbt.CompoundTag;

public class BlockData {
    private double qualityBonus;

    public double useQuality() {
        double bonus = qualityBonus;
        qualityBonus = 0;
        return bonus;
    }

    public double getQuality() {
        return qualityBonus;
    }

    public void incrementQuality(double value) {
        qualityBonus += value;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("quality_bonus", qualityBonus);

        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        qualityBonus = tag.getDouble("quality_bonus");
    }
}
