package de.cadentem.quality_food.util;

import de.cadentem.quality_food.client.ClientProxy;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.BlockData;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.data.QFBlockTags;
import de.cadentem.quality_food.data.QFItemTags;
import de.cadentem.quality_food.network.CookingParticles;
import de.cadentem.quality_food.registry.QFComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class Utils {
    /** Safety measure to avoid trying to apply quality multiple times to the same item */
    public static final ThreadLocal<ItemStack> LAST_STACK = new ThreadLocal<>();

    public static boolean isValidItem(final ItemStack stack) {
        return isValidItem(stack, true);
    }

    public static boolean isValidItem(final ItemStack stack, boolean checkBlock) {
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

        if (checkBlock && stack.getItem() instanceof BlockItem blockItem && isValidBlock(blockItem.getBlock(), false)) {
            return true;
        }

        return stack.is(QFItemTags.MATERIAL_WHITELIST);
    }

    public static boolean isValidBlock(final Block block) {
        return isValidBlock(block, true);
    }

    @SuppressWarnings("deprecation")
    public static boolean isValidBlock(final Block block, boolean checkItem) {
        if (block.builtInRegistryHolder().is(QFBlockTags.QUALITY_BLOCKS)) {
            return true;
        } else if (checkItem) {
            return isValidItem(block.asItem().getDefaultInstance(), false);
        }

        return false;
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

    public static BlockState applyQuality(final BlockState grown, final ServerLevel level, final BlockPos position, final Direction direction) {
        return applyQuality(grown, level, position, position.relative(direction));
    }

    public static BlockState applyQuality(final BlockState grown, final ServerLevel level, final BlockPos position, final BlockPos grownPosition) {
        if (Utils.isValidBlock(grown.getBlock())) {
            LevelData data = level.getData(AttachmentHandler.LEVEL_DATA);
            Quality quality = data.get(position);

            if (/* Don't apply PLAYER_PLACED */ quality.level() > 0) {
                data.set(grownPosition, quality);
            }
        }

        return grown;
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
