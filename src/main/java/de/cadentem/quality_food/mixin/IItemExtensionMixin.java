package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.util.FoodUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IItemExtension.class)
public class IItemExtensionMixin {
    @ModifyReturnValue(method = "getFoodProperties", at = @At("RETURN"))
    private FoodProperties quality_food$modifyFoodProperties(final FoodProperties original, /* Parameters: */ final ItemStack stack) {
        if (QualityUtils.hasQuality(stack)) {
            return FoodUtils.handleFoodProperties(stack, original);
        }

        return original;
    }
}
