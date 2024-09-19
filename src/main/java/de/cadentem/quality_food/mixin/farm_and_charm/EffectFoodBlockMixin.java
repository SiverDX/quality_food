package de.cadentem.quality_food.mixin.farm_and_charm;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.FoodUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.satisfy.farm_and_charm.block.EffectFoodBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

/** Modify food properties and effects */
@Mixin(EffectFoodBlock.class)
public abstract class EffectFoodBlockMixin {
    @ModifyArg(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private int quality_food$modifyNutrition(int nutrition, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        return (int) (nutrition * LevelData.get(level, position).getType().nutritionMultiplier());
    }

    @ModifyArg(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private float quality_food$modifyNutrition(float saturation, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        return (float) (saturation * LevelData.get(level, position).getType().saturationMultiplier());
    }

    @WrapOperation(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean quality_food$modifyEffect(final Player player, final MobEffectInstance instance, final Operation<Boolean> original, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        Optional<MobEffectInstance> optional = FoodUtils.modifyEffect(instance, LevelData.get(level, position).getType());

        if (optional.isPresent()) {
            return original.call(player, optional.get());
        }

        return false;
    }
}
