package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessing;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(value = FanProcessing.class, remap = false)
public abstract class FanProcessingMixin {
    @ModifyVariable(method = "applyProcessing(Lnet/minecraft/world/entity/item/ItemEntity;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Z", at = @At(value = "STORE"), name = "stacks")
    private static List<ItemStack> quality_food$applyQuality(final List<ItemStack> result, @Local(argsOnly = true) final ItemEntity input) {
        if (result == null || result.isEmpty()) {
            return result;
        }

        result.forEach(stack -> QualityUtils.applyQuality(stack, List.of(input.getItem()), null, input.registryAccess()));
        return result;
    }

    @ModifyVariable(method = "applyProcessing(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/world/level/Level;Lcom/simibubi/create/content/kinetics/fan/processing/FanProcessingType;)Lcom/simibubi/create/content/kinetics/belt/behaviour/TransportedItemStackHandlerBehaviour$TransportedResult;", at = @At("STORE"), name = "stacks")
    private static List<ItemStack> quality_food$applyQuality(final List<ItemStack> result, @Local(argsOnly = true) final TransportedItemStack ingredient, @Local(argsOnly = true) final Level level) {
        if (result == null) {
            return null;
        }

        result.forEach(stack -> QualityUtils.applyQuality(stack, List.of(ingredient.stack), null, level.registryAccess()));
        return result;
    }
}
