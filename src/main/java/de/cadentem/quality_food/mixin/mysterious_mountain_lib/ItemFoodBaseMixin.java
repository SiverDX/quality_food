package de.cadentem.quality_food.mixin.mysterious_mountain_lib;

import cn.mcmod_mmf.mmlib.item.ItemFoodBase;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.FoodUtils;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(ItemFoodBase.class)
public abstract class ItemFoodBaseMixin {
    // The mod overrides the base food property retrieval from NeoForge
    @ModifyReturnValue(method = "getFoodProperties", at = @At("RETURN"))
    private @Nullable FoodProperties quality_food$handleFoodProperties(final FoodProperties original, @Local(argsOnly = true) final ItemStack stack) {
        return FoodUtils.handleFoodProperties(stack, original);
    }
}
