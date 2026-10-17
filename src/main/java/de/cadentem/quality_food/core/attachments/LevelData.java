package de.cadentem.quality_food.core.attachments;

import java.util.Map;
import de.cadentem.quality_food.mixin.LevelMixin;
import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.core.codecs.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@ParametersAreNonnullByDefault
public class LevelData implements ValueIOSerializable {
    private final HashMap<Long, Quality> qualities = new HashMap<>();
    private @Nullable Pair<Long, Quality> lastRemoved;

    public @NotNull Quality get(final BlockPos position) {
        Quality quality = qualities.get(position.asLong());

        if (quality == null) {
            return Quality.NONE;
        }

        return quality;
    }

    public void set(final BlockPos position, final Quality quality) {
        if (quality == Quality.NONE) {
            remove(position);
            return;
        }

        qualities.put(position.asLong(), quality);
    }

    public void remove(final BlockPos position) {
        long key = position.asLong();
        Quality removed = qualities.remove(key);
        lastRemoved = Pair.of(key, removed == null ? Quality.NONE : removed);
    }

    public static void set(final LevelAccessor level, final BlockPos position, final Quality quality) {
        if (level instanceof ServerLevel serverLevel && quality != Quality.NONE) {
            LevelData data = serverLevel.getData(AttachmentHandler.LEVEL_DATA);
            data.set(position, quality);
        }
    }

    /** @return The stored quality or the last removed quality (since {@link LevelMixin} happens before the loot drops) if the flag is set to true */
    public static @NotNull Quality get(final LevelAccessor level, final BlockPos position, boolean queryLastRemoved) {
        Quality result = Quality.NONE;

        if (level instanceof ServerLevel serverLevel) {
            LevelData data = serverLevel.getData(AttachmentHandler.LEVEL_DATA);
            result = data.get(position);

            if (queryLastRemoved && data.lastRemoved != null && result == Quality.NONE && data.lastRemoved.getFirst() == position.asLong()) {
                result = data.lastRemoved.getSecond();
            }
        }

        return result;
    }

    public static @NotNull Quality get(final LevelAccessor level, final BlockPos position) {
        return get(level, position, false);
    }

    public static void remove(final LevelAccessor level, final BlockPos position) {
        if (level instanceof ServerLevel serverLevel) {
            LevelData data = serverLevel.getData(AttachmentHandler.LEVEL_DATA);
            data.remove(position);
        }
    }

    @Override
    public void serialize(final ValueOutput output) {
        Map<String, Quality> entries = new HashMap<>();
        qualities.forEach((position, quality) -> entries.put(position.toString(), quality));
        output.store("qualities", Codec.unboundedMap(Codec.STRING, Quality.CODEC), entries);
    }

    @Override
    public void deserialize(final ValueInput input) {
        qualities.clear();
        input.read("qualities", Codec.unboundedMap(Codec.STRING, Quality.CODEC)).orElseThrow()
                .forEach((position, quality) -> qualities.put(Long.parseLong(position), quality));
    }
}
