package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.common.block.entity.AbstractStoveBlockEntity;

@Mixin(value = AbstractStoveBlockEntity.class, remap = false)
public abstract class AbstractStoveBlockEntityMixin {
    @ModifyVariable(method = "cookAndOutputItems", at = @At(value = "STORE"), name = "result")
    private ItemStack quality_food$applyQuality(final ItemStack result, @Local(name = "ingredient") final ItemStack ingredient) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(ingredient));
        return result;
    }
}
