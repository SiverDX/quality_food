package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.foundation.recipe.trie.RecipeTrie;
import com.simibubi.create.foundation.recipe.trie.RecipeTrieFinder;
import de.cadentem.quality_food.compat.create.RecipeMapping;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(RecipeTrieFinder.class)
public abstract class RecipeTrieFinderMixin {
    @Inject(method = "lambda$get$0", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/recipe/trie/RecipeTrie$Builder;insert(Lnet/minecraft/world/item/crafting/Recipe;)V"))
    private static void quality_food$cacheRecipe(final Object cacheKey, final Level level, final Predicate<RecipeHolder<? extends Recipe<?>>> conditions, final CallbackInfoReturnable<RecipeTrie<?>> callback, @Local(name = "recipe") RecipeHolder<?> recipe) {
        RecipeMapping.RECIPES.put(recipe.value(), recipe);
    }
}
