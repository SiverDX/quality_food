package de.cadentem.quality_food.core.attachments;

import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ParametersAreNonnullByDefault
public class BlockData implements INBTSerializable<CompoundTag> {
    private final Set<Holder<QualityType>> cookedQualities = new HashSet<>();
    private double qualityBonus;

    public void useQuality(final ItemStack stack, @Nullable final Player player) {
        Holder<QualityType> chosenType = null;

        for (Holder<QualityType> type : cookedQualities) {
            if (chosenType == null || type.value().level() < chosenType.value().level()) {
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

    public void addQualityType(final Holder<QualityType> type) {
        cookedQualities.add(type);
    }

    @Override
    public @NotNull CompoundTag serializeNBT(final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("quality_bonus", qualityBonus);
        CompoundTag types = new CompoundTag();

        for (Holder<QualityType> type : cookedQualities) {
            Optional<ResourceKey<QualityType>> optional = type.unwrapKey();
            // doesn't matter what the actual value is
            optional.ifPresent(qualityTypeResourceKey -> types.putBoolean(qualityTypeResourceKey.location().toString(), true));
        }

        tag.put("types", types);
        return tag;
    }

    @Override
    public void deserializeNBT(final HolderLookup.Provider provider, final CompoundTag tag) {
        qualityBonus = tag.getDouble("quality_bonus");
        cookedQualities.clear();

        CompoundTag types = tag.getCompound("types");

        for (String location : types.getAllKeys()) {
            Optional<Holder.Reference<QualityType>> optional = provider.lookupOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).get(ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, ResourceLocation.parse(location)));
            optional.ifPresent(cookedQualities::add);
        }
    }
}
