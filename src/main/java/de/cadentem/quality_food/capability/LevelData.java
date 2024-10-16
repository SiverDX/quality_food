package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.core.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class LevelData {
    private final HashMap<Long, Quality> qualities = new HashMap<>();

    private @Nullable Quality lastRemoved;

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
        lastRemoved = qualities.remove(position.asLong());
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        for (Long key : qualities.keySet()) {
            tag.putInt(String.valueOf(key), qualities.get(key).ordinal());
        }

        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        qualities.clear();

        tag.getAllKeys().forEach(key -> {
            Quality quality = Quality.get(tag.getInt(String.valueOf(key)), true);
            qualities.put(Long.parseLong(key), quality);
        });
    }

    public static void set(final LevelAccessor level, final BlockPos position, final Quality quality) {
        if (quality != Quality.NONE) {
            LevelDataProvider.getCapability(level).ifPresent(data -> data.set(position, quality));
        }
    }

    /** @return The stored quality or the last removed quality (since {@link de.cadentem.quality_food.mixin.LevelMixin} happens before the loot drops) if the flag is set to true */
    public static @NotNull Quality get(final LevelAccessor level, @Nullable final BlockPos position, boolean queryLastRemoved) {
        LevelData data = LevelDataProvider.getOrNull(level);

        if (data == null || position == null) {
            return Quality.NONE;
        }

        Quality result = data.get(position);

        if (queryLastRemoved && data.lastRemoved != null && result == Quality.NONE) {
            result = data.lastRemoved;
            data.lastRemoved = null;
        }

        return result;
    }

    public static @NotNull Quality get(final LevelAccessor level, final BlockPos position) {
        return get(level, position, false);
    }
}
