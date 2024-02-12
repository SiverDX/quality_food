package de.cadentem.quality_food.util;

import de.cadentem.quality_food.QualityFood;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.block.FeastBlock;

public class Utils {
    public static boolean isFeastBlock(final ItemStack stack) {
        if (!QualityFood.isModLoaded(QualityFood.FARMERSDELIGHT)) {
            return false;
        }

        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof FeastBlock;
    }
}
