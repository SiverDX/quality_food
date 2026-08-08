package de.cadentem.quality_food.capability;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.core.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.HashMap;
import java.util.List;

public class LevelData {
    private final HashMap<Long, Quality> qualities = new HashMap<>();
    /** For mods that allow placing items on the ground / stack them (e.g. 'Display Delight') */
    private final HashMap<Long, List<ItemStack>> storedItemStacks = new HashMap<>();

    private @Nullable Pair<Long, Quality> lastRemoved;

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

        if (queryLastRemoved && data.lastRemoved != null && result == Quality.NONE && data.lastRemoved.getFirst() == position.asLong()) {
            result = data.lastRemoved.getSecond();
        }

        return result;
    }

    public static @NotNull Quality get(final LevelAccessor level, final BlockPos position) {
        return get(level, position, false);
    }

    public static void storeItem(final LevelAccessor level, final BlockPos position, final ItemStack stack) {
        LevelData data = LevelDataProvider.getOrNull(level);

        if (data == null || position == null) {
            return;
        }

        data.storedItemStacks.computeIfAbsent(position.asLong(), key -> List.of()).add(stack);
    }

    public static @Unmodifiable List<ItemStack> getStoredItems(final LevelAccessor level, final BlockPos position) {
        LevelData data = LevelDataProvider.getOrNull(level);

        if (data == null || position == null) {
            return List.of();
        }

        return data.storedItemStacks.getOrDefault(position.asLong(), List.of());
    }

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
        lastRemoved = Pair.of(key, qualities.remove(key));
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();

        for (Long key : qualities.keySet()) {
            tag.putInt(String.valueOf(key), qualities.get(key).ordinal());
        }

        CompoundTag storedItems = new CompoundTag();

        storedItemStacks.forEach((key, value) -> {
            ListTag entry = new ListTag();
            value.forEach(stack -> entry.add(stack.save(new CompoundTag())));
            storedItems.put(String.valueOf(key), entry);
        });

        tag.put("stored_items", storedItems);

        return tag;
    }

    public void deserializeNBT(final CompoundTag tag) {
        qualities.clear();

        tag.getAllKeys().forEach(key -> {
            if (key.equals("stored_items")) {
                storedItemStacks.clear();
                CompoundTag storedItems = tag.getCompound("stored_items");

                storedItems.getAllKeys().forEach(position -> {
                    ListTag items = storedItems.getList(position, ListTag.TAG_COMPOUND);

                    for (int i = 0; i < items.size(); i++) {
                        ItemStack stack = ItemStack.of(items.getCompound(i));
                        storedItemStacks.computeIfAbsent(Long.parseLong(position), k -> List.of()).add(stack);
                    }
                });

                return;
            }

            Quality quality = Quality.get(tag.getInt(key), true);
            qualities.put(Long.parseLong(key), quality);
        });
    }
}
