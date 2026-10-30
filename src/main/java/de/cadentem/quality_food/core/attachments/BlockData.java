package de.cadentem.quality_food.core.attachments;

import de.cadentem.quality_food.core.Modification;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@ParametersAreNonnullByDefault
public class BlockData implements ValueIOSerializable {
    private final Deque<CookingEntry> cookingQueue = new ArrayDeque<>();
    private @Nullable Quality previewQuality;

    public void useQuality(final ItemStack stack, @Nullable final Player player, final Level level) {
        if (cookingQueue.isEmpty()) {
            return;
        }

        // The rolled quality may have already been shown (e.g. through a storage terminal) - taking it out should not re-roll it
        if (previewQuality != null && stack.getCount() <= getHeadRunLength()) {
            Quality preview = previewQuality;
            consumeQuality(stack.getCount());

            if (QualityUtils.isValidQuality(preview)) {
                QualityUtils.applyQuality(stack, preview, true);
            }

            return;
        }

        previewQuality = null;

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

        for (Holder<QualityType> type : level.registryAccess().lookupOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).listElements().toList()) {
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

    public ItemStack previewQuality(final ItemStack stack, final Level level) {
        if (cookingQueue.isEmpty() || stack.isEmpty()) {
            return stack;
        }

        if (previewQuality == null) {
            CookingEntry entry = cookingQueue.peek();
            Holder<QualityType> selected = entry != null ? entry.type() : null;
            double finalBonus = entry != null ? entry.bonus() : 0;

            for (Holder<QualityType> type : level.registryAccess().lookupOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).listElements().toList()) {
                if (selected != null && type.value().level() <= selected.value().level()) {
                    continue;
                }

                double chance = level.getRandom().nextDouble();
                chance = Modification.additive((float) finalBonus / (type.value().level() * type.value().level())).apply(chance);

                if (chance > 1 - type.value().chance()) {
                    selected = type;
                }
            }

            previewQuality = selected != null ? QualityType.createQuality(selected, stack) : Quality.NONE;
        }

        if (!QualityUtils.isValidQuality(previewQuality)) {
            return stack;
        }

        ItemStack copy = stack.copy();
        QualityUtils.applyQuality(copy, previewQuality, true);
        return copy;
    }

    /**
     * Removes the oldest entries until the queue is no larger than the given size <br>
     * The result slot can change without consuming the queue (e.g. recipe book, commands or other mods setting the slot directly)
     * @return Whether entries were removed
     */
    public boolean trimQueue(int size) {
        if (cookingQueue.size() <= size) {
            return false;
        }

        while (cookingQueue.size() > size) {
            cookingQueue.poll();
        }

        previewQuality = null;
        return true;
    }

    public void consumeQuality(int count) {
        for (int i = 0; i < count; i++) {
            cookingQueue.poll();
        }
        previewQuality = null;
    }

    public Snapshot createSnapshot() {
        return new Snapshot(List.copyOf(cookingQueue), previewQuality);
    }

    public void restoreSnapshot(final Snapshot snapshot) {
        cookingQueue.clear();
        cookingQueue.addAll(snapshot.entries());
        previewQuality = snapshot.previewQuality();
    }

    public double getQueueSize() {
        return cookingQueue.size();
    }

    /** The number of consecutive entries at the head of the queue which are equal to the head entry */
    public int getHeadRunLength() {
        CookingEntry head = cookingQueue.peek();

        if (head == null) {
            return 0;
        }

        int length = 0;

        for (CookingEntry entry : cookingQueue) {
            if (!head.isSameAs(entry)) {
                break;
            }

            length++;
        }

        return length;
    }

    public void addQualityEntry(final Holder<QualityType> type, double bonus) {
        cookingQueue.add(new CookingEntry(type, bonus));
    }

    @Override
    public void serialize(final ValueOutput output) {
        ValueOutput.ValueOutputList queue = output.childrenList("cooking_queue");
        for (CookingEntry entry : cookingQueue) {
            ValueOutput entryOutput = queue.addChild();
            entry.type().unwrapKey().ifPresent(key -> entryOutput.putString("type", key.identifier().toString()));
            entryOutput.putDouble("bonus", entry.bonus());
        }
    }

    @Override
    public void deserialize(final ValueInput input) {
        cookingQueue.clear();
        previewQuality = null;
        for (ValueInput entry : input.childrenListOrEmpty("cooking_queue")) {
            double bonus = entry.getDoubleOr("bonus", 0);
            Holder<QualityType> type = entry.getString("type")
                    .map(Identifier::tryParse)
                    .map(id -> ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, id))
                    .flatMap(key -> input.lookup().lookupOrThrow(QFComponents.QUALITY_TYPE_REGISTRY).get(key))
                    .map(holder -> (Holder<QualityType>) holder)
                    .orElseGet(() -> Holder.direct(QualityType.NONE));
            cookingQueue.add(new CookingEntry(type, bonus));
        }
    }

    public record CookingEntry(Holder<QualityType> type, double bonus) {
        public boolean isSameAs(final CookingEntry other) {
            if (Double.compare(bonus, other.bonus) != 0) {
                return false;
            }

            return type.unwrapKey().equals(other.type.unwrapKey()) && (type.unwrapKey().isPresent() || type.value() == other.type.value());
        }
    }
    public record Snapshot(List<CookingEntry> entries, @Nullable Quality previewQuality) { }
}
