package de.cadentem.quality_food.mixin.create;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(value = CrushingWheelControllerBlockEntity.class, remap = false)
public abstract class CrushingWheelControllerBlockEntityMixin {
    @ModifyArg(method = "applyRecipe", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/item/ItemHelper;addToList(Lnet/minecraft/world/item/ItemStack;Ljava/util/List;)V", ordinal = 0))
    private ItemStack quality_food$applyQuality(final ItemStack result, @Local(ordinal = 0) final ItemStack input) {
        QualityUtils.applyQuality(result, List.of(input), null);
        return result;
    }
}
