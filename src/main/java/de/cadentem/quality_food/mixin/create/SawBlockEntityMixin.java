package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = SawBlockEntity.class, remap = false)
public abstract class SawBlockEntityMixin {
    @Shadow public ProcessingInventory inventory;

    @Inject(method = "applyRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/recipe/ProcessingInventory;clear()V", shift = At.Shift.BEFORE))
    private void quality_food$storeInput(final CallbackInfo callback, @Share("input") final LocalRef<ItemStack> input) {
        input.set(inventory.getStackInSlot(0));
    }

    @ModifyArg(method = "applyRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/recipe/ProcessingInventory;setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack quality_food$applyQuality(final ItemStack result, @Share("input") final LocalRef<ItemStack> input) {
        QualityUtils.applyQuality(result, List.of(input.get()), null);
        return result;
    }
}
