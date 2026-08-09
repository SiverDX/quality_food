package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import de.cadentem.quality_food.compat.SpecialContainer;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(value = BasinRecipe.class, remap = false)
public abstract class BasinRecipeMixin {
    @ModifyArg(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;acceptOutputs(Ljava/util/List;Ljava/util/List;Z)Z"), index = 0)
    private static List<ItemStack> quality_food$applyQuality(final List<ItemStack> stacks, @Local final IItemHandler availableItems, @Local(ordinal = 0) int[] extractedItemsFromSlot, @Local(argsOnly = true) Recipe<?> recipe, @Local(argsOnly = true) BasinBlockEntity basin) {
        SpecialContainer container = new SpecialContainer(18);

        for (int slot = 0; slot < extractedItemsFromSlot.length; slot++) {
            if (extractedItemsFromSlot[slot] == 0) {
                container.setItem(slot, ItemStack.EMPTY);
                continue;
            }

            ItemStack ingredient = availableItems.getStackInSlot(slot).copy();
            ingredient.setCount(extractedItemsFromSlot[slot]);
            container.setItem(slot, ingredient);
        }

        for (ItemStack stack : stacks) {
            //noinspection DataFlowIssue -> level is not null at this point
            QualityUtils.handleConversion(stack, container, recipe, basin.getLevel());

            if (!QualityUtils.hasQuality(stack) && !ServerConfig.isNoQualityRecipe(recipe, basin.getLevel())) {
                QualityUtils.applyQuality(stack, container.getIngredients(), null);
            }
        }

        return stacks;
    }
}
