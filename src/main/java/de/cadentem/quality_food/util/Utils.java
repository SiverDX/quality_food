package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.BlockData;
import de.cadentem.quality_food.client.ClientProxy;
import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.data.QFBlockTags;
import de.cadentem.quality_food.data.QFItemTags;
import de.cadentem.quality_food.network.CookingParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class Utils {
    /** Safety measure to avoid trying to apply quality multiple times to the same item */
    public static final ThreadLocal<ItemStack> LAST_STACK = new ThreadLocal<>();
    /** Bonus from the block entity for menu usage */
    public static final ThreadLocal<BlockPos> BLOCK_ENTITY_POSITION = new ThreadLocal<>();

    public static boolean isValidItem(final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.is(QFItemTags.BLACKLIST)) {
            return false;
        }

        FoodProperties properties = stack.getFoodProperties(null);

        if (properties != null && (properties.nutrition() > 0 || properties.saturation() > 0)) {
            return true;
        }

        if (isValidBlock(stack)) {
            return true;
        }

        return stack.is(QFItemTags.MATERIAL_WHITELIST);
    }

    public static boolean isValidBlock(final ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            return isValidBlock(blockItem.getBlock());
        }

        return false;
    }

    public static boolean isValidBlock(final Block block) {
        return block.builtInRegistryHolder().is(QFBlockTags.QUALITY_BLOCKS);
    }

    public static @Nullable BlockPos getBlockEntityPosition() {
        BlockPos position = BLOCK_ENTITY_POSITION.get();

        if (position != null) {
            BLOCK_ENTITY_POSITION.remove();
            return position;
        }

        return null;
    }

    public static void sendParticles(final ServerLevel serverLevel, final BlockEntity furnace, final BlockPos position) {
        int tickOffset = serverLevel.getRandom().nextInt(-3, 3);

        if (serverLevel.getGameTime() % (10 + tickOffset) == 0) {
            BlockData blockData = furnace.getData(AttachmentHandler.BLOCK_DATA);
            double qualityBonus = blockData.getQuality();

            if (qualityBonus > 0) {
                PacketDistributor.sendToPlayersNear(serverLevel, null, position.getX(), position.getY(), position.getZ(), 64, new CookingParticles(position, qualityBonus));
            }
        }
    }

    public static void incrementQuality(final BlockEntity blockEntity, final ItemStack stack) {
        incrementQuality(blockEntity, stack, 1);
    }

    public static void incrementQuality(final BlockEntity blockEntity, final ItemStack stack, int ingredientCount) {
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide() || ingredientCount < 1) {
            return;
        }

        QualityType type = QualityUtils.getType(stack);

        if (type != QualityType.NONE) {
            BlockData blockData = blockEntity.getData(AttachmentHandler.BLOCK_DATA);
            blockData.incrementQuality(type.cookingBonus() / ingredientCount);
            blockEntity.setChanged();
        }
    }

    public static @Nullable Registry<QualityType> getQualityRegistry() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server != null) {
            return server.registryAccess().registry(QFComponents.QUALITY_TYPE_REGISTRY).orElse(null);
        } else if (FMLEnvironment.dist.isClient()) {
            return ClientProxy.getQualityRegistry();
        }

        return null;
    }
}
