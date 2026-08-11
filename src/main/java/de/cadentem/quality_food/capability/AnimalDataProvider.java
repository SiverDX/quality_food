package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.data.QFEntityTypeTags;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class AnimalDataProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Map<UUID, LazyOptional<AnimalData>> SERVER_CACHE = new HashMap<>();

    private final AnimalData data = new AnimalData();
    private final LazyOptional<AnimalData> instance = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull final Capability<T> capability, @Nullable final Direction side) {
        return capability == CapabilityHandler.ANIMAL_DATA_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).serializeNBT();
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        instance.orElseThrow(() -> new IllegalArgumentException("Capability instance was not present")).deserializeNBT(tag);
    }

    /** Seems more stable than emptying it on stopping the server */
    @SubscribeEvent
    public static void clearCache(final ServerStartedEvent event) {
        SERVER_CACHE.clear();
    }

    @SubscribeEvent
    public static void removeCacheEntry(final EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        SERVER_CACHE.remove(event.getEntity().getUUID());
    }

    public static LazyOptional<AnimalData> getCapability(@Nullable final Mob mob) {
        if (mob == null || mob.level().isClientSide() || mob.getType().is(QFEntityTypeTags.ANIMAL_DATA_BLACKLIST)) {
            return LazyOptional.empty();
        }

        UUID key = mob.getUUID();
        LazyOptional<AnimalData> capability = SERVER_CACHE.get(key);

        if (capability == null) {
            capability = mob.getCapability(CapabilityHandler.ANIMAL_DATA_CAPABILITY);

            if (capability.isPresent()) {
                SERVER_CACHE.put(key, capability);
            }
        }

        return capability;
    }
}
