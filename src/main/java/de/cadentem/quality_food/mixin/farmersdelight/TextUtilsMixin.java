package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.FoodUtils;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.common.utility.TextUtils;

/** Access food properties through the stack (i.e. have context of tag data) */
@Mixin(value = TextUtils.class, remap = false)
public class TextUtilsMixin {
    @ModifyVariable(method = "addFoodEffectTooltip", at = @At("STORE"))
    private static FoodProperties quality_food$switchCall(final FoodProperties properties, @Local(argsOnly = true) final ItemStack stack) {
        return FoodUtils.handleFoodProperties(stack, properties);
    }
}
