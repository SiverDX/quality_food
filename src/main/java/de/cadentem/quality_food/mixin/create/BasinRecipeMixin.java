package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Collection;

@Mixin(value = BasinRecipe.class, remap = false)
public abstract class BasinRecipeMixin {
    @ModifyArg(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    private static Object quality_food$applyQuality(final Object object, /* It's there, not an issue */ @Local(name = "remainderContainer") final CraftingContainer container, @Local(argsOnly = true) Recipe<?> recipe, @Local(argsOnly = true) BasinBlockEntity basin) {
        if (!(object instanceof ItemStack result)) {
            // Mixin cannot handle the generic parameter / type of the list
            return object;
        }

        // We get the direct result from the recipe - any modifications will impact any future crafting results
        result = result.copy();

        //noinspection DataFlowIssue -> level is not null at this point
        QualityUtils.handleConversion(result, container, recipe, basin.getLevel());

        if (!QualityUtils.hasQuality(result) && !ServerConfig.isNoQualityRecipe(recipe, basin.getLevel())) {
            QualityUtils.applyQuality(result, container.getItems(), null);
        }

        return result;
    }

    @ModifyArg(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"))
    private static Collection<Object> quality_food$applyQualityMultiple(@NotNull final Collection<Object> results, @Local(name = "remainderContainer") final CraftingContainer container, @Local(argsOnly = true) Recipe<?> recipe, @Local(argsOnly = true) BasinBlockEntity basin  /* It's there, not an issue */) {
        for (Object object : results) {
            if (!(object instanceof ItemStack result)) {
                // Mixin cannot handle the generic parameter / type of the list
                continue;
            }

            // We get the direct result from the recipe - any modifications will impact any future crafting results
            result = result.copy();

            //noinspection DataFlowIssue -> level is not null at this point
            QualityUtils.handleConversion(result, container, recipe, basin.getLevel());

            if (!QualityUtils.hasQuality(result) && !ServerConfig.isNoQualityRecipe(recipe, basin.getLevel())) {
                QualityUtils.applyQuality(result, container.getItems(), null);
            }
        }

        return results;
    }
}
