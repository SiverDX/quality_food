package de.cadentem.quality_food.mixin.sophisticatedcore;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.p3pp3rf1y.sophisticatedcore.util.RecipeHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Makes sure that the result has the same quality as the input (for compacting / un-compacting) */
@Mixin(value = RecipeHelper.class, remap = false)
public abstract class RecipeHelperMixin {
    @ModifyVariable(method = "getCompactingResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;IILjava/util/Map;)Lnet/p3pp3rf1y/sophisticatedcore/util/RecipeHelper$CompactingResult;", at = @At("STORE"), ordinal = 1)
    private static ItemStack quality_food$applyQualityMultipleRecipeMatches(final ItemStack result, final ItemStack input) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(input));
        return result;
    }

    @Inject(method = "cacheAndGetCompactingResult(Lnet/p3pp3rf1y/sophisticatedcore/util/RecipeHelper$CompactedItem;Lnet/minecraft/world/item/crafting/CraftingRecipe;Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/item/ItemStack;)Lnet/p3pp3rf1y/sophisticatedcore/util/RecipeHelper$CompactingResult;", at = @At("HEAD"))
    private static void quality_food$applyQualitySingleRecipeMatch(@Coerce final Object compactedItem, final CraftingRecipe recipe, final CraftingContainer craftingInventory, final ItemStack result, final CallbackInfoReturnable<RecipeHelper.CompactingResult> callback) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(((CompactedItemAccess) compactedItem).quality_food$getItem()));
    }

    @ModifyReturnValue(method = "lambda$getUncompactResultItems$9", at = @At("RETURN"))
    private static ItemStack quality_food$applyQualityUncompacting(final ItemStack result, @Local(argsOnly = true) final CraftingContainer container) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(container.asCraftInput().getItem(0)));
        return result;
    }
}
