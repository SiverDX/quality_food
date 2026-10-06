package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import de.cadentem.quality_food.compat.create.RecipeMapping;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(value = BasinRecipe.class, remap = false)
public abstract class BasinRecipeMixin {
    /** Handles the result of non-basin recipes (e.g. crafting recipes) */
    @ModifyArg(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 2))
    private static Object quality_food$applyQuality(final Object object, @Local(name = "availableItems") final IItemHandler availableItems, @Local(name = "extractedItemsFromSlot") final int[] extractedItemsFromSlot, @Local(argsOnly = true) final Recipe<?> recipe, @Local(argsOnly = true) final BasinBlockEntity basin) {
        if (!(object instanceof ItemStack result)) {
            // Mixin cannot handle the generic parameter / type of the list
            return object;
        }

        // We get the direct result from the recipe - any modifications would impact future crafting results
        result = result.copy();
        quality_food$handle(result, quality_food$toContainer(availableItems, extractedItemsFromSlot), recipe, basin);

        return result;
    }

    /** Handles the (rolled) results of basin recipes */
    @ModifyArg(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"))
    private static Collection<Object> quality_food$applyQualityMultiple(@NotNull final Collection<Object> results, @Local(name = "availableItems") final IItemHandler availableItems, @Local(name = "extractedItemsFromSlot") final int[] extractedItemsFromSlot, @Local(argsOnly = true) final Recipe<?> recipe, @Local(argsOnly = true) final BasinBlockEntity basin) {
        List<Object> modified = new ArrayList<>(results.size());
        SimpleContainer container = quality_food$toContainer(availableItems, extractedItemsFromSlot);

        for (Object object : results) {
            if (!(object instanceof ItemStack result)) {
                // Mixin cannot handle the generic parameter / type of the list
                modified.add(object);
                continue;
            }

            // We get the direct result from the recipe - any modifications would impact future crafting results
            result = result.copy();
            quality_food$handle(result, container, recipe, basin);
            modified.add(result);
        }

        return modified;
    }

    @Unique
    private static void quality_food$handle(final ItemStack result, final SimpleContainer container, final Recipe<?> recipe, final BasinBlockEntity basin) {
        RecipeHolder<?> holder = RecipeMapping.RECIPES.get(recipe);

        if (holder == null || basin.getLevel() == null) {
            return;
        }

        RegistryAccess access = basin.getLevel().registryAccess();
        QualityUtils.handleConversion(result, container, holder, access);

        if (!QualityUtils.hasQuality(result) && !ServerConfig.isNoQualityRecipe(holder, access)) {
            QualityUtils.applyQuality(result, Utils.collectIngredients(container, container::getContainerSize), null, access);
        }
    }

    /**
     * Mirrors what Create does for its remainder container (the extracted amount per slot) <br>
     * The stacks are split up so that each item is counted individually (relevant for the storage block detection) </br>
     * (DummyCraftingContainer is only present on 'simulate')
     */
    @Unique
    private static SimpleContainer quality_food$toContainer(final IItemHandler availableItems, final int[] extractedItemsFromSlot) {
        List<ItemStack> split = new ArrayList<>();

        for (int slot = 0; slot < extractedItemsFromSlot.length && slot < availableItems.getSlots(); slot++) {
            ItemStack stack = availableItems.getStackInSlot(slot);

            if (stack.isEmpty()) {
                continue;
            }

            for (int i = 0; i < extractedItemsFromSlot[slot]; i++) {
                split.add(stack.copyWithCount(1));
            }
        }

        return new SimpleContainer(split.toArray(ItemStack[]::new));
    }
}
