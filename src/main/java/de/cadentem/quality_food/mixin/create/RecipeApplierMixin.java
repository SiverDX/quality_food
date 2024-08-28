package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import de.cadentem.quality_food.config.QualityConfig;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = RecipeApplier.class, remap = false)
public abstract class RecipeApplierMixin {
    @ModifyArg(method = "applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;)Ljava/util/List;", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/item/ItemHelper;multipliedOutput(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"), index = 1)
    private static ItemStack quality_food$applyQuality(final ItemStack result, @Local(argsOnly = true) final ItemStack ingredient) {
        Quality quality = QualityUtils.getQuality(ingredient);
        float multiplier = 1;

        if (quality.level() > 0) {
            multiplier = 0.6f / QualityConfig.getChance(quality);
        }

        QualityUtils.applyQuality(result, Bonus.multiplicative(multiplier));
        return result;
    }
}
