package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.core.Modification;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class BlockData {
    private static final RandomSource RANDOM = RandomSource.create();

    private final Set<Quality> cookedQualities = new HashSet<>();
    private double qualityBonus;

    public void useQuality(final ItemStack stack, @Nullable final Player player) {
        Quality selected = null;

        for (Quality quality : cookedQualities) {
            if (selected == null || quality.level() < selected.level()) {
                selected = quality;
            }
        }

        if (selected != null) {
            QualityUtils.applyQuality(stack, selected);
        } else {
            selected = Quality.NONE;
        }

        for (Quality quality : Quality.values()) {
            if (quality.level() == 0) {
                return;
            }

            double chance = RANDOM.nextDouble();
            chance = Modification.luck(player).apply(chance);
            chance = Modification.additive((float) qualityBonus).apply(chance);

            if (chance >= 1 - QualityConfig.getChance(quality)) {
                selected = quality;
            }
        }

        QualityUtils.applyQuality(stack, selected, true);

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
