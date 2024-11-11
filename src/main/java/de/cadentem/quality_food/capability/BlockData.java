package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockData {
    private final Set<Quality> cookedQualities = new HashSet<>();
    private double qualityBonus;

    public void useQuality(final ItemStack stack, @Nullable final Player player) {
        Quality chosenQuality = null;

        for (Quality quality : cookedQualities) {
            if (chosenQuality == null || quality.level() < chosenQuality.level()) {
                chosenQuality = quality;
            }
        }

        if (chosenQuality != null) {
            QualityUtils.applyQuality(stack, chosenQuality);
        }

        QualityUtils.applyQuality(stack, player, List.of(Bonus.additive((float) qualityBonus)), true);

        qualityBonus = 0;
        cookedQualities.clear();
    }

    public double getQuality() {
        return qualityBonus;
    }

    public void incrementQuality(double value) {
        qualityBonus += value;
    }


    public void addQualityType(final Quality quality) {
        cookedQualities.add(quality);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("quality_bonus", qualityBonus);

        CompoundTag types = new CompoundTag();

        for (Quality quality : cookedQualities) {
            types.putInt(quality.getName(), quality.ordinal());
        }

        tag.put("types", types);

        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        qualityBonus = tag.getDouble("quality_bonus");
        cookedQualities.clear();

        CompoundTag types = tag.getCompound("types");

        for (String name : types.getAllKeys()) {
            cookedQualities.add(Quality.get(types.getInt(name)));
        }
    }
}
