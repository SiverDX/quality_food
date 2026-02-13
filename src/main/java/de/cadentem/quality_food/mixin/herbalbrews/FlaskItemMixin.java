package de.cadentem.quality_food.mixin.herbalbrews;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.FoodUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.satisfy.herbalbrews.core.items.FlaskItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FlaskItem.class, remap = false)
public abstract class FlaskItemMixin {
    @ModifyVariable(method = "applyPotionEffects", at = @At("STORE"), name = "effectInstance")
    private MobEffectInstance quality_food$modifyEffect(final MobEffectInstance instance, @Local(argsOnly = true) final ItemStack stack) {
        return FoodUtils.modifyEffect(instance, QualityUtils.getQuality(stack)).orElse(instance);
    }
}
