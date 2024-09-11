package de.cadentem.quality_food.capability;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CapabilityHandler {
    public static final Capability<BlockData> BLOCK_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<LevelData> LEVEL_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation BLOCK_DATA = new ResourceLocation(QualityFood.MODID, "block_data");
    public static final ResourceLocation LEVEL_DATA = new ResourceLocation(QualityFood.MODID, "level_data");

    @SubscribeEvent
    public static void attachBlockEntityCapability(final AttachCapabilitiesEvent<BlockEntity> event) {
        if (BlockDataProvider.isValid(event.getObject())) {
            event.addCapability(BLOCK_DATA, new BlockDataProvider());
            event.addListener(() -> BlockDataProvider.SERVER_CACHE.remove(event.getObject().getBlockPos().asLong()));
        }
    }

    @SubscribeEvent
    public static void attachLevelCapability(final AttachCapabilitiesEvent<Level> event) {
        event.addCapability(LEVEL_DATA, new LevelDataProvider());
        event.addListener(() -> LevelDataProvider.CACHE.remove(event.getObject()));
    }
}