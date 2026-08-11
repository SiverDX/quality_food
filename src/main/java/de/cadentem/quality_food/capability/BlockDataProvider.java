package de.cadentem.quality_food.capability;

import com.sammy.minersdelight.setup.MDBlockEntities;
import de.cadentem.quality_food.compat.Compat;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.ribs.vintagedelight.block.entity.ModBlockEntities;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class BlockDataProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Long2ObjectMap<LazyOptional<BlockData>> SERVER_CACHE = new Long2ObjectOpenHashMap<>();

    private final BlockData data = new BlockData();
    private final LazyOptional<BlockData> instance = LazyOptional.of(() -> data);

    public static LazyOptional<BlockData> getCapability(@Nullable final BlockEntity entity) {
        if (entity == null || entity.getLevel() == null || entity.getLevel().isClientSide() || !isValid(entity)) {
            return LazyOptional.empty();
        }

        long key = entity.getBlockPos().asLong();
        LazyOptional<BlockData> capability = SERVER_CACHE.get(key);

        if (capability == null) {
            capability = entity.getCapability(CapabilityHandler.BLOCK_DATA_CAPABILITY);
            capability.addListener(ignored -> SERVER_CACHE.remove(key));

            if (capability.isPresent()) {
                SERVER_CACHE.put(key, capability);
            }
        }

        return capability;
    }

    @SuppressWarnings("RedundantIfStatement") // ignore for clarity
    public static boolean isValid(final BlockEntity entity) {
        if (entity instanceof AbstractFurnaceBlockEntity) {
            return true;
        }

        if (Compat.Mod.FARMERSDELIGHT.isLoaded() && entity.getType() == ModBlockEntityTypes.COOKING_POT.get()) {
            return true;
        }

        if (Compat.Mod.MINERS_DELIGHT.isLoaded() && entity.getType() == MDBlockEntities.COPPER_POT.get()) {
            return true;
        }

        if (Compat.Mod.HERALBREWS.isLoaded() && entity.getType() == EntityTypeRegistry.TEA_KETTLE_BLOCK_ENTITY.get()) {
            return true;
        }

        if (Compat.Mod.VINTAGEDELIGHT.isLoaded() && entity.getType() == ModBlockEntities.FERMENTING_JAR_BE.get()) {
            return true;
        }

        return false;
    }

    /** Seems more stable than emptying it on stopping the server */
    @SubscribeEvent
    public static void clearCache(final ServerStartedEvent event) {
        SERVER_CACHE.clear();
    }

    @SubscribeEvent
    public static void removeCacheEntry(final BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        SERVER_CACHE.remove(event.getPos().asLong());
    }

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
}
