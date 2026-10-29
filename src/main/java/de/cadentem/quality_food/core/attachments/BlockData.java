package de.cadentem.quality_food.core.attachments;

import de.cadentem.quality_food.core.Modification;
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

    public void consumeQuality(int count) {
        for (int i = 0; i < count; i++) {
            cookingQueue.poll();
        }
    }

    public List<CookingEntry> createSnapshot() {
        return List.copyOf(cookingQueue);
    }

    public void restoreSnapshot(final List<CookingEntry> snapshot) {
        cookingQueue.clear();
        cookingQueue.addAll(snapshot);
    }

    public double getQueueSize() {
        return cookingQueue.size();
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

    public record CookingEntry(Holder<QualityType> type, double bonus) { }
}
