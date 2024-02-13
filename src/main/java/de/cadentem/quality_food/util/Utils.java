package de.cadentem.quality_food.util;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.data.QFItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import vectorwing.farmersdelight.common.block.FeastBlock;

public class Utils {
    /** The fallback in `ItemMixin` could cause multiple attempts for the same stack */
    public static final ThreadLocal<ItemStack> LAST_STACK = new ThreadLocal<>();
    public static final IntegerProperty QUALITY_STATE = IntegerProperty.create(QualityUtils.QUALITY_TAG, 0, Quality.values().length - 1);

    public static boolean isValidItem(final ItemStack stack) {
        if (stack.getFoodProperties(null) != null) {
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
        boolean isValid = block instanceof StemGrownBlock || block instanceof CakeBlock || block instanceof CandleCakeBlock;

        if (isValid) {
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
}
