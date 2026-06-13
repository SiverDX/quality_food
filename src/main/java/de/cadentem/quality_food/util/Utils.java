package de.cadentem.quality_food.util;

import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.attachments.BlockData;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.data.QFBlockTags;
import de.cadentem.quality_food.data.QFItemTags;
import de.cadentem.quality_food.network.CookingParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class Utils {
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
            BlockData data = furnace.getData(AttachmentHandler.BLOCK_DATA);
            double qualityBonus = data.getQuality();

            if (qualityBonus > 0.1) {
                PacketDistributor.sendToPlayersNear(serverLevel, null, position.getX(), position.getY(), position.getZ(), 64, new CookingParticles(position, qualityBonus));
            }
        }
    }

    /**
     * Store quality of a crop that has grown in another direction
     * @param grown The new state
     * @param position The position of the base crop (not the newly grown position), to retrieve the current quality
     * @param direction The position in which the crop grows
     */
    public static void storeQuality(final BlockState grown, final ServerLevel accessor, final BlockPos position, final Direction direction) {
        storeQuality(grown, accessor, position, position.relative(direction), 1);
    }

    public static void storeQuality(final BlockState grown, final ServerLevel accessor, final BlockPos position, final BlockPos grownPosition) {
        storeQuality(grown, accessor, position, grownPosition, 1);
    }

    /**
     * Used to store quality to a new position (based on the quality of the passed position) <br>
     * Usually used to store quality to a crop which has a higher height than 1 when growing
     */
    public static void storeQuality(final BlockState grown, final ServerLevel level, final BlockPos position, final BlockPos grownPosition, double chance) {
        if (Utils.isValidBlock(grown.getBlock())) {
            LevelData data = level.getData(AttachmentHandler.LEVEL_DATA);
            Quality quality = data.get(position);

            if (Math.random() > chance) {
                quality = Quality.getRandom(ItemStack.EMPTY, quality.level() - 1);
            }

            if (/* Don't apply PLAYER_PLACED */ quality.level() > 0) {
                data.set(grownPosition, quality);
            }
        }
    }

    public static void incrementQuality(final BlockEntity blockEntity, final ItemStack stack) {
        incrementQuality(blockEntity, stack, 1, 64);
    }

    public static void incrementQuality(final BlockEntity blockEntity, final ItemStack stack, int ingredientCount, int resultStackSize) {
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide() || ingredientCount < 1) {
            return;
        }

        if (!Utils.isValidItem(stack)) {
            return;
        }

        Holder<QualityType> type = QualityUtils.getType(stack);
        BlockData data = blockEntity.getData(AttachmentHandler.BLOCK_DATA);
        data.addQualityType(type);

        if (type.value() != QualityType.NONE) {
            // Lower stack size result in a higher bonus so that the intended bonus will be reached
            double bonus = type.value().cookingBonus() * (64d / resultStackSize);
            data.incrementQuality(bonus / ingredientCount);
        }

        blockEntity.setChanged();
    }

    public static void useQuality(final BlockEntity block, final ItemStack stack, @Nullable final Player player) {
        BlockData data = block.getData(AttachmentHandler.BLOCK_DATA);
        //noinspection DataFlowIssue -> level is present
        data.useQuality(stack, player, block.getLevel());
        block.setChanged();
    }
}
