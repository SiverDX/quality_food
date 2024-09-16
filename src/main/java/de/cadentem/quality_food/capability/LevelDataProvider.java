package de.cadentem.quality_food.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber
public class LevelDataProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Map<Level, LazyOptional<LevelData>> CACHE = new HashMap<>();

    private final LevelData data = new LevelData();
    private final LazyOptional<LevelData> instance = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction side) {
        return capability == CapabilityHandler.LEVEL_DATA_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).serializeNBT();
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).deserializeNBT(tag);
    }

    public static LazyOptional<LevelData> getCapability(@Nullable final LevelAccessor accessor) {
        if (!(accessor instanceof Level level)) {
            return LazyOptional.empty();
        }

        LazyOptional<LevelData> capability = CACHE.get(level);

        if (capability == null) {
            capability = level.getCapability(CapabilityHandler.LEVEL_DATA_CAPABILITY);

            if (capability.isPresent()) {
                CACHE.put(level, capability);
            }
        }

        return capability;
    }

    public static @Nullable LevelData getOrNull(@Nullable final LevelAccessor accessor) {
        LazyOptional<LevelData> optional = getCapability(accessor);

        if (optional.isPresent()) {
            Optional<LevelData> resolved = optional.resolve();

            if (resolved.isPresent()) {
                return resolved.get();
            }
        }

        return null;
    }

    @SubscribeEvent
    public static void removeCacheEntry(final LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            CACHE.remove(level);
        }
    }
}
