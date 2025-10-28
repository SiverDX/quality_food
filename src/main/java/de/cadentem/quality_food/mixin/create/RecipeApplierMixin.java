package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = RecipeApplier.class, remap = false)
public abstract class RecipeApplierMixin {
    /** Used by fans or mechanical press */
    @Inject(method = "applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;Z)Ljava/util/List;", at = @At(value = "RETURN"))
    private static void quality_food$applyQuality(final Level level, final ItemStack input, final Recipe<?> recipe, final boolean returnProcessingRemainder, final CallbackInfoReturnable<List<ItemStack>> callback) {
        callback.getReturnValue().forEach(stack -> QualityUtils.applyQuality(stack, List.of(input), null));
    }

    /** Used my mechanical press */
    @ModifyVariable(method = "applyRecipeOn(Lnet/minecraft/world/entity/item/ItemEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)V", at = @At("STORE"))
    private static List<ItemStack> quality_food$applyQuality(final List<ItemStack> result, @Local(argsOnly = true) final ItemEntity ingredient) {
        result.forEach(stack -> QualityUtils.applyQuality(stack, List.of(ingredient.getItem()), null));
        return result;
    }
}
