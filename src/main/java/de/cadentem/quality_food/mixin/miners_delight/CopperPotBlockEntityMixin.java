package de.cadentem.quality_food.mixin.miners_delight;

import com.sammy.minersdelight.content.block.copper_pot.CopperPotBlockEntity;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

@Mixin(value = CopperPotBlockEntity.class, remap = false)
public abstract class CopperPotBlockEntityMixin {
    @Shadow @Final private ItemStackHandler inventory;

    /** Display particles to show how much quality the block has stored */
    @Inject(method = "cookingTick", at = @At("TAIL"))
    private static void quality_food$handleParticles(final Level level, final BlockPos position, final BlockState state, final CopperPotBlockEntity blockEntity, final CallbackInfo callback) {
        if (level instanceof ServerLevel serverLevel) {
            Utils.sendParticles(serverLevel, blockEntity, position);
        }
    }

    /** Increment quality after cooking an item */
    @ModifyVariable(method = "processCooking", at = @At(value = "STORE"), name = "resultStack", remap = false)
    private ItemStack quality_food$incrementQuality(final ItemStack resultStack, final CookingPotRecipe recipe, final CopperPotBlockEntity cookingPot) {
        int resultStackSize = 1;

        if (cookingPot.getLevel() != null) {
            resultStackSize = recipe.getResultItem(cookingPot.getLevel().registryAccess()).getCount();
        }

        Utils.incrementQuality(cookingPot, Utils.collectIngredients(inventory, () -> 4), resultStackSize);
        return resultStack;
    }
}
