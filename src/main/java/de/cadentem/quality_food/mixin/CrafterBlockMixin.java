package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Apply quality when the item is crafted and dispensed */
@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {
    @ModifyVariable(method = "dispenseFrom", at = @At(value = "STORE", target = "Lnet/minecraft/world/item/crafting/CraftingRecipe;assemble(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack quality_food$handleConversion(final ItemStack result, @Local(argsOnly = true) final ServerLevel level, @Local final CrafterBlockEntity crafter, @Local final RecipeHolder<CraftingRecipe> recipe) {
        QualityUtils.handleConversion(result, crafter, recipe, level.registryAccess());

        if (!ServerConfig.isNoQualityRecipe(recipe, level.registryAccess())) {
            QualityUtils.applyQuality(result, crafter.getItems(), null, level.registryAccess());
        }

        return result;
    }
}
