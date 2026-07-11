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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

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

                if (qualityBonus >= 0.1) {
                    NetworkHandler.CHANNEL.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(position.getX(), position.getY(), position.getZ(), 64, serverLevel.dimension())), new SyncCookingParticle(position, qualityBonus));
                }
            });
        }
    }

    /**
     * Store quality of a crop that has grown in another direction
     * @param grown The new state
     * @param position The position of the base crop (not the newly grown position), to retrieve the current quality
     * @param direction The position in which the crop grows
     */
    public static void storeQuality(final BlockState grown, final LevelAccessor accessor, final BlockPos position, final Direction direction) {
        storeQuality(grown, accessor, position, position.relative(direction), 1);
    }

    public static void storeQuality(final BlockState grown, final LevelAccessor accessor, final BlockPos position, final BlockPos grownPosition) {
        storeQuality(grown, accessor, position, grownPosition, 1);
    }

    /**
     * Used to store quality to a new position (based on the quality of the passed position) <br>
     * Usually used to store quality to a crop which has a higher height than 1 when growing
     */
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

    /**
     * Collects quality-applicable items from the given inventory
     * @param maxSlotCheck To determine up to which slot the items should be considered
     */
    public static Collection<ItemStack> collectIngredients(final ItemStackHandler handler, final Supplier<Integer> maxSlotCheck) {
        List<ItemStack> ingredients = new ArrayList<>();

        for (int slot = 0; slot < maxSlotCheck.get(); slot++) {
            ItemStack ingredient = handler.getStackInSlot(slot);

            if (!Utils.isValidItem(ingredient)) {
                continue;
            }

            ingredients.add(ingredient);
        }

        return ingredients;
    }

    /**
     * Collects the cooking bonus from the ingredients and the lowest quality type present </br>
     * This is then added to the cooking queue which will be used to apply the cooking bonus / quality to the result item
     */
    public static void incrementQuality(final BlockEntity blockEntity, @Unmodifiable final Collection<ItemStack> ingredients, int resultStackSize) {
        if (blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide() || ingredients.isEmpty()) {
            return;
        }

        BlockDataProvider.getCapability(blockEntity).ifPresent(data -> {
            double qualityBonus = 0;
            Quality selected = Quality.NONE;

            for (ItemStack ingredient : ingredients) {
                Quality quality = QualityUtils.getQuality(ingredient);

                if (quality != Quality.NONE) {
                    // Lower stack size result in a higher bonus so that the intended bonus will be reached
                    qualityBonus += QualityUtils.getCookingBonus(ingredient, resultStackSize) / ingredients.size();
                }

                if (selected.level() < quality.level()) {
                    selected = quality;
                }
            }

            data.addQualityEntry(selected, qualityBonus);
            blockEntity.setChanged();
        });
    }

    public static void useQuality(final BlockEntity block, final ItemStack stack, @Nullable final Player player) {
        BlockDataProvider.getCapability(block).ifPresent(data -> data.useQuality(stack, player));
        block.setChanged();
    }
}
