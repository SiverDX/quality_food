package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(value = CookingPotBlockEntity.class, remap = false)
public abstract class CookingPotBlockEntityMixin {
    /** Display particles to show how much quality the block has stored */
    @Inject(method = "cookingTick", at = @At("TAIL"))
    private static void quality_food$handleParticles(final Level level, final BlockPos position, final BlockState state, final CookingPotBlockEntity blockEntity, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            Utils.sendParticles(serverLevel, blockEntity, position);
        }
    }

    /** Increment quality after cooking an item */
    @Inject(method = "processCooking", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE, remap = true))
    private void quality_food$incrementQuality(final CookingPotRecipe recipe, final CookingPotBlockEntity blockEntity, final CallbackInfoReturnable<Boolean> callback, @Local(ordinal = 2) final ItemStack stack) {
        Utils.incrementQuality(blockEntity, stack, recipe.getIngredients().size()); // TODO :: Consider max. stack size when taking out? Since it can keep cooking while you are only able to take out 16 e.g.
    }
}
