package de.cadentem.quality_food.attachments;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.component.Quality;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

public class LevelData implements INBTSerializable<CompoundTag> {
    private final HashMap<Long, Quality> qualities = new HashMap<>();

    public Quality get(final BlockPos position) {
        Quality quality = qualities.get(position.asLong());

        if (quality == null) {
            return Quality.NONE;
        }

        return quality;
    }

    public void set(final BlockPos position, final Quality quality) {
        qualities.put(position.asLong(), quality);
    }

    public void remove(final BlockPos position) {
        qualities.remove(position.asLong());
    }

    public CompoundTag serializeNBT(@NotNull final HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        for (Long key : qualities.keySet()) {
            Optional<Tag> qualityTag = Quality.CODEC.encodeStart(NbtOps.INSTANCE, qualities.get(key)).resultOrPartial(QualityFood.LOG::error);
            qualityTag.ifPresent(value -> tag.put(String.valueOf(key), value));
        }

        return tag;
    }

    public void deserializeNBT(@NotNull final HolderLookup.Provider provider, final CompoundTag tag) {
        qualities.clear();

        tag.getAllKeys().forEach(key -> {
            Quality quality = Quality.CODEC.parse(NbtOps.INSTANCE, tag.getCompound(key)).resultOrPartial(QualityFood.LOG::error).orElse(Quality.NONE);
            qualities.put(Long.parseLong(key), quality);
        });
    }

    public static @NotNull Quality get(@NotNull final LevelAccessor level, @NotNull final BlockPos position) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getData(AttachmentHandler.LEVEL_DATA).get(position);
        }

        return Quality.NONE;
    }
}
