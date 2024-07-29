package de.cadentem.quality_food.core.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BlockData implements INBTSerializable<CompoundTag> {
    private double qualityBonus;

    public float useQuality() {
        double bonus = qualityBonus;
        qualityBonus = 0;
        return (float) bonus;
    }

    public double getQuality() {
        return qualityBonus;
    }

    public void incrementQuality(double value) {
        qualityBonus += value;
    }

    @Override
    public @NotNull CompoundTag serializeNBT(final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("quality_bonus", qualityBonus);

        return tag;
    }

    @Override
    public void deserializeNBT(final HolderLookup.Provider provider, final CompoundTag tag) {
        qualityBonus = tag.getDouble("quality_bonus");
    }
}
