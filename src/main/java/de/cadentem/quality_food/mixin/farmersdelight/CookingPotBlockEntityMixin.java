package de.cadentem.quality_food.mixin.farmersdelight;

import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(value = CookingPotBlockEntity.class)
public abstract class CookingPotBlockEntityMixin {
    @Shadow @Final private ItemStackHandler inventory;

    /** Display particles to show how much quality the block has stored */
    @Inject(method = "cookingTick", at = @At("TAIL"), remap = false)
    private static void quality_food$handleParticles(final Level level, final BlockPos position, final BlockState state, final CookingPotBlockEntity blockEntity, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            Utils.sendParticles(serverLevel, blockEntity, position);
        }
    }

    /** Increment quality after cooking an item */
    @ModifyVariable(method = "processCooking", at = @At(value = "STORE"), name = "resultStack")
    private ItemStack quality_food$incrementQuality(final ItemStack resultStack, final RecipeHolder<CookingPotRecipe> recipe, final CookingPotBlockEntity cookingPot) {
        int resultStackSize = 64;

        if (cookingPot.getLevel() != null) {
            resultStackSize = recipe.value().getResultItem(cookingPot.getLevel().registryAccess()).getMaxStackSize();
        }

        Utils.incrementQuality(cookingPot, Utils.collectIngredients(inventory, () -> 6), resultStackSize);
        return resultStack;
    }
}
