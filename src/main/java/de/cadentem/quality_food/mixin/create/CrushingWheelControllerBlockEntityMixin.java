package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = CrushingWheelControllerBlockEntity.class, remap = false)
public abstract class CrushingWheelControllerBlockEntityMixin {
    @Shadow private RecipeWrapper wrapper;

    @Inject(method = "applyRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/recipe/ProcessingInventory;clear()V", shift = At.Shift.BEFORE, ordinal = 0))
    private void quality_food$storeInput(final CallbackInfo callback, @Share("input") final LocalRef<ItemStack> input) {
        input.set(wrapper.getItem(0));
    }

    @ModifyVariable(method = "applyRecipe", at = @At("STORE"))
    private ItemStack quality_food$applyQuality(final ItemStack result, @Share("input") final LocalRef<ItemStack> input) {
        QualityUtils.applyQuality(result, List.of(input.get()), null);
        return result;
    }
}
