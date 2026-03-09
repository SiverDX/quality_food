package de.cadentem.quality_food.mixin.herbalbrews;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.util.FoodUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.satisfy.herbalbrews.core.items.FlaskItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(value = FlaskItem.class, remap = false)
public abstract class FlaskItemMixin {
    @WrapOperation(method = "applyPotionEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/alchemy/PotionContents;forEachEffect(Ljava/util/function/Consumer;)V", ordinal = 0))
    private void quality_food$modifyEffect(final PotionContents instance, final Consumer<MobEffectInstance> consumer, final Operation<Void> original, @Local(argsOnly = true) final ItemStack stack, @Local(argsOnly = true) final Player player) {
        if (QualityUtils.hasQuality(stack)) {
            instance.getAllEffects().forEach(effect -> {
                QualityType type = QualityUtils.getType(stack).value();
                player.addEffect(FoodUtils.modifyEffect(effect, type).orElse(effect));
            });
        } else {
            original.call(instance, consumer);
        }
    }
}
