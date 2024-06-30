package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.compat.Compat;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

import java.util.HashMap;
import java.util.Map;

public class BlockDataProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Map<Long, LazyOptional<BlockData>> SERVER_CACHE = new HashMap<>();

    private final BlockData data = new BlockData();
    private final LazyOptional<BlockData> instance = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction side) {
        return capability == CapabilityHandler.BLOCK_DATA_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).serializeNBT();
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).deserializeNBT(tag);
    }

    public static LazyOptional<BlockData> getCapability(@Nullable final BlockEntity entity) {
        if (entity == null || entity.getLevel() == null || entity.getLevel().isClientSide() || !isValid(entity)) {
            return LazyOptional.empty();
        }

        long key = entity.getBlockPos().asLong();
        LazyOptional<BlockData> capability = SERVER_CACHE.get(key);

        if (capability == null) {
            capability = entity.getCapability(CapabilityHandler.BLOCK_DATA_CAPABILITY);

            if (capability.isPresent()) {
                SERVER_CACHE.put(key, capability);
            }
        }

        return capability;
    }

    public static boolean isValid(final BlockEntity entity) {
        if (entity instanceof AbstractFurnaceBlockEntity) {
            return true;
        }

        if (Compat.isModLoaded(Compat.FARMERSDELIGHT)) {
            return entity.getType() == ModBlockEntityTypes.COOKING_POT.get();
        }

        return false;
    }
}
