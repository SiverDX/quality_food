package de.cadentem.quality_food.core.attachments;

import de.cadentem.quality_food.core.Modification;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@ParametersAreNonnullByDefault
public class BlockData implements INBTSerializable<CompoundTag> {
    private final Deque<CookingEntry> cookingQueue = new ArrayDeque<>();

    public void useQuality(final ItemStack stack, @Nullable final Player player, final Level level) {
        if (cookingQueue.isEmpty()) {
            return;
        }

        Set<Holder<QualityType>> qualities = new TreeSet<>(Comparator.comparingInt(quality -> quality.value().level()));
        double qualityBonus = 0;

        for (int i = 0; i < stack.getCount(); i++) {
            CookingEntry entry = cookingQueue.poll();

            // In case a quality is removed from the registry before taking out the items
            if (entry != null) {
                qualityBonus += entry.bonus();
                qualities.add(entry.type());
            }
        }

        double finalBonus = qualityBonus / stack.getCount();
        Holder<QualityType> selected = qualities.stream().findFirst().orElse(null);

        // Apply the lowest ingredient quality as base
        // The cooking bonus can cause it to upgrade to a higher tier
        if (selected != null) {
            QualityUtils.applyQuality(stack, selected);
        }

        for (Holder<QualityType> type : level.registryAccess().registryOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).holders().toList()) {
            if (selected != null && type.value().level() <= selected.value().level()) {
                continue;
            }

            double chance = level.getRandom().nextDouble();
            chance = Modification.luck(player).apply(chance);
            chance = Modification.additive((float) finalBonus / (type.value().level() * type.value().level())).apply(chance);

            if (chance > 1 - type.value().chance()) {
                selected = type;
            }
        }

        if (selected != null) {
            QualityUtils.applyQuality(stack, QualityType.createQuality(selected, stack), true);
        }
    }

    public double getQuality() {
        return cookingQueue.stream().mapToDouble(CookingEntry::bonus).sum();
    }

    public void addQualityEntry(final Holder<QualityType> type, double bonus) {
        cookingQueue.add(new CookingEntry(type, bonus));
    }

    @Override
    public @NotNull CompoundTag serializeNBT(final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag queue = new ListTag();

        for (CookingEntry entry : cookingQueue) {
            CompoundTag entryTag = new CompoundTag();
            entry.type().unwrapKey().ifPresent(key -> entryTag.putString("type", key.location().toString()));
            entryTag.putDouble("bonus", entry.bonus());
            queue.add(entryTag);
        }

        tag.put("cooking_queue", queue);
        return tag;
    }

    @Override
    public void deserializeNBT(final HolderLookup.Provider provider, final CompoundTag tag) {
        cookingQueue.clear();

        ListTag queue = tag.getList("cooking_queue", Tag.TAG_COMPOUND);

        for (int i = 0; i < queue.size(); i++) {
            CompoundTag entryTag = queue.getCompound(i);
            Optional<Holder.Reference<QualityType>> optional = provider.lookupOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).get(ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, ResourceLocation.parse(entryTag.getString("type"))));
            optional.ifPresent(type -> cookingQueue.add(new CookingEntry(type, entryTag.getDouble("bonus"))));
        }
    }

    public record CookingEntry(Holder<QualityType> type, double bonus) { }
}
