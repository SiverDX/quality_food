package de.cadentem.quality_food.util;

import de.cadentem.quality_food.capability.BlockDataProvider;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.capability.LevelDataProvider;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.data.QFBlockTags;
import de.cadentem.quality_food.data.QFItemTags;
import de.cadentem.quality_food.network.NetworkHandler;
import de.cadentem.quality_food.network.SyncCookingParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

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

        if (properties != null && (properties.getNutrition() > 0 || properties.getSaturationModifier() > 0)) {
            return true;
        }

        if (checkBlock && stack.getItem() instanceof BlockItem blockItem && isValidBlock(blockItem.getBlock())) {
            return true;
        }

        return stack.is(QFItemTags.MATERIAL_WHITELIST);
    }

    public static boolean isValidBlock(final BlockState state) {
        return isValidBlock(state, true);
    }

    public static boolean isValidBlock(final BlockState state, boolean checkItem) {
        if (state.is(QFBlockTags.QUALITY_BLOCKS)) {
            return true;
        } else if (checkItem) {
            return isValidItem(state.getBlock().asItem().getDefaultInstance(), false);
        }

        return false;
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

    public static void sendParticles(final ServerLevel serverLevel, final BlockEntity blockEntity, final BlockPos position) {
        int tickOffset = serverLevel.getRandom().nextInt(-3, 3);

        if (serverLevel.getGameTime() % (10 + tickOffset) == 0) {
            BlockDataProvider.getCapability(blockEntity).ifPresent(data -> {
                double qualityBonus = data.getQuality();

                if (qualityBonus > 0) {
                    NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.getX(), position.getY(), position.getZ(), 64, serverLevel.dimension())), new SyncCookingParticle(position, qualityBonus));
                }
            });
        }
    }

    public static void incrementQuality(final BlockEntity blockEntity, final ItemStack stack) {
        incrementQuality(blockEntity, stack, 1);
    }

    public static void incrementQuality(final BlockEntity blockEntity, final ItemStack stack, int ingredientCount) {
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide() || ingredientCount < 1) {
            return;
        }

        BlockDataProvider.getCapability(blockEntity).ifPresent(data -> data.incrementQuality(QualityUtils.getCookingBonus(stack) / ingredientCount));
    }

    public static void storeQuality(final BlockState grown, final LevelAccessor accessor, final BlockPos position, final Direction direction) {
        storeQuality(grown, accessor, position, position.relative(direction), 1);
    }

    public static void storeQuality(final BlockState grown, final LevelAccessor accessor, final BlockPos position, final BlockPos grownPosition) {
        storeQuality(grown, accessor, position, grownPosition, 1);
    }

    public static void storeQuality(final BlockState grown, final LevelAccessor accessor, final BlockPos position, final BlockPos grownPosition, double chance) {
        if (Utils.isValidBlock(grown.getBlock())) {
            LevelData data = LevelDataProvider.getOrNull(accessor);

            if (data == null) {
                return;
            }

            Quality quality = data.get(position);

            if (Math.random() > chance) {
                quality = Quality.get(quality.ordinal() - 1);
            }

            if (quality.level() > 0) {
                data.set(grownPosition, quality);
            }
        }
    }
}
