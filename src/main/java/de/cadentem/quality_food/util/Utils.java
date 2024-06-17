package de.cadentem.quality_food.util;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.capability.BlockDataProvider;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.data.QFItemTags;
import de.cadentem.quality_food.network.NetworkHandler;
import de.cadentem.quality_food.network.SyncCookingParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.block.FeastBlock;

public class Utils {
    /** Safety measure to avoid trying to apply quality multiple times to the same item */
    public static final ThreadLocal<ItemStack> LAST_STACK = new ThreadLocal<>();
    /** Bonus from the block entity for menu usage */
    public static final ThreadLocal<BlockPos> BLOCK_ENTITY_POSITION = new ThreadLocal<>();
    public static final IntegerProperty QUALITY_STATE = IntegerProperty.create(QualityUtils.QUALITY_TAG, 0, Quality.values().length - 1);

    public static boolean isValidItem(final ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if (stack.is(QFItemTags.BLACKLIST)) {
            return false;
        }

        FoodProperties properties = stack.getFoodProperties(null);

        if (properties != null) {
            return properties.getNutrition() > 0 || properties.getSaturationModifier() > 0;
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
        if (block instanceof BushBlock || block instanceof StemGrownBlock || block instanceof VineBlock || block instanceof CakeBlock || block instanceof CandleCakeBlock) {
            return true;
        }

        if (QualityFood.isModLoaded(QualityFood.FARMERSDELIGHT)) {
            return block instanceof FeastBlock;
        }

        return false;
    }

    public static boolean isValidBlock(final BlockState state) {
        return isValidBlock(state.getBlock());
    }

    public static @Nullable BlockPos getBlockEntityPosition() {
        BlockPos position = BLOCK_ENTITY_POSITION.get();

        if (position != null) {
            BLOCK_ENTITY_POSITION.remove();
            return position;
        }

        return null;
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
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide()) {
            return;
        }

        BlockDataProvider.getCapability(blockEntity).ifPresent(data -> data.incrementQuality(QualityUtils.getCookingBonus(stack)));
    }
}
