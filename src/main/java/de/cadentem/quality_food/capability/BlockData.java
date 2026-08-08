package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.core.Modification;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Set;
import java.util.TreeSet;

public class BlockData {
    private static final RandomSource RANDOM = RandomSource.create();
    private final Deque<CookingEntry> cookingQueue = new ArrayDeque<>();

    public void useQuality(final ItemStack stack, @Nullable final Player player) {
        if (cookingQueue.isEmpty()) {
            return;
        }

        Set<Quality> qualities = new TreeSet<>(Comparator.comparingInt(Quality::level));
        double qualityBonus = 0;

        for (int i = 0; i < stack.getCount(); i++) {
            CookingEntry entry = cookingQueue.poll();

            // In case a quality is removed from the registry before taking out the items
            if (entry != null) {
                qualityBonus += entry.bonus();
                qualities.add(entry.quality());
            }
        }

        double finalBonus = qualityBonus / stack.getCount();
        Quality selected = qualities.stream().findFirst().orElse(Quality.NONE);

        // Apply the lowest ingredient quality as base
        // The cooking bonus can cause it to upgrade to a higher tier
        if (selected != Quality.NONE) {
            QualityUtils.applyQuality(stack, selected);
        }

        for (Quality quality : Quality.values()) {
            if (quality.level() == 0) {
                continue;
            }

            double chance = RANDOM.nextDouble();
            chance = Modification.luck(player).apply(chance);
            chance = Modification.additive((float) finalBonus / (quality.level() * quality.level())).apply(chance);

            if (chance > 1 - QualityConfig.getChance(quality)) {
                selected = quality;
            }
        }

        if (selected != Quality.NONE) {
            QualityUtils.applyQuality(stack, selected, true);
        }
    }

    public double getQuality() {
        return cookingQueue.stream().mapToDouble(CookingEntry::bonus).sum();
    }

    public void addQualityEntry(final Quality quality, double bonus) {
        cookingQueue.add(new CookingEntry(quality, bonus));
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag queue = new ListTag();

        for (CookingEntry entry : cookingQueue) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("quality", entry.quality().ordinal());
            entryTag.putDouble("bonus", entry.bonus());
            queue.add(entryTag);
        }

        tag.put("cooking_queue", queue);
        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        cookingQueue.clear();

        ListTag queue = tag.getList("cooking_queue", Tag.TAG_COMPOUND);

        for (int i = 0; i < queue.size(); i++) {
            CompoundTag entryTag = queue.getCompound(i);
            cookingQueue.add(new CookingEntry(Quality.get(entryTag.getInt("quality")), entryTag.getDouble("bonus")));
        }
    }

    public record CookingEntry(Quality quality, double bonus) { }
}

