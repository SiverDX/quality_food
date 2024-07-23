package de.cadentem.quality_food.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class BlockData implements INBTSerializable<CompoundTag> {
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

    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("quality_bonus", qualityBonus);

        return tag;
    }

    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, final CompoundTag tag) {
        qualityBonus = tag.getDouble("quality_bonus");
    }
}
