package de.cadentem.quality_food.core.attachments;

import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ParametersAreNonnullByDefault
public class BlockData implements INBTSerializable<CompoundTag> {
    private final Set<QualityType> cookedQualities = new HashSet<>();
    private double qualityBonus;

    public void useQuality(final ItemStack stack, @Nullable final Player player) {
        QualityType chosenType = null;

        for (QualityType type : cookedQualities) {
            if (chosenType == null || type.level() < chosenType.level()) {
                chosenType = type;
            }
        }

        if (chosenType != null) {
            QualityUtils.applyQuality(stack, chosenType);
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

    public void addQualityType(final QualityType type) {
        cookedQualities.add(type);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("quality_bonus", qualityBonus);

        Registry<QualityType> registry = Utils.getQualityRegistry();
        CompoundTag types = new CompoundTag();

        for (QualityType type : cookedQualities) {
            //noinspection DataFlowIssue -> registry should be present
            ResourceLocation key = registry.getKey(type);
            //noinspection DataFlowIssue -> location should exist
            types.putBoolean(key.toString(), true); // doesn't matter what the actual value is
        }

        tag.put("types", types);
        return tag;
    }

    @Override
    public void deserializeNBT(final HolderLookup.Provider provider, final CompoundTag tag) {
        qualityBonus = tag.getDouble("quality_bonus");
        cookedQualities.clear();

        CompoundTag types = tag.getCompound("types");
        Registry<QualityType> registry = Utils.getQualityRegistry();

        for (String location : types.getAllKeys()) {
            //noinspection DataFlowIssue -> registry should be present
            QualityType type = registry.get(ResourceLocation.parse(location));

            if (type != null) {
                cookedQualities.add(type);
            }
        }
    }
}
