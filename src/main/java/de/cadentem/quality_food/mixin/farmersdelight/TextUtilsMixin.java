package de.cadentem.quality_food.mixin.farmersdelight;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.common.utility.TextUtils;

@Mixin(value = TextUtils.class, remap = false)
public class TextUtilsMixin {
    @ModifyVariable(method = "addFoodEffectTooltip", at = @At("STORE"))
    private static FoodProperties switchCall(final FoodProperties foodProperties, /* Method parameters: */ final ItemStack stack) {
        return stack.getFoodProperties(null);
    }
}
