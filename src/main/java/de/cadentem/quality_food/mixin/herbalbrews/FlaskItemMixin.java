package de.cadentem.quality_food.mixin.herbalbrews;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.FoodUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.satisfy.herbalbrews.core.items.FlaskItem;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(value = FlaskItem.class, remap = false)
@Debug(export = true)
public abstract class FlaskItemMixin {
    @WrapOperation(method = "applyPotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;forEachEffect(Ljava/util/function/Consumer;)V"))
    private void quality_food$modifyEffect(final PotionContents instance, final Consumer<MobEffectInstance> instanceConsumer, final Operation<Void> original, @Local(argsOnly = true) final ItemStack stack) {
        // FIXME
//        return FoodUtils.modifyEffect(instance, QualityUtils.getQuality(stack)).orElse(instance);
    }
}
