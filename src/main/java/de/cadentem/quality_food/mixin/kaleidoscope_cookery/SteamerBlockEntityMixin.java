package de.cadentem.quality_food.mixin.kaleidoscope_cookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SteamerBlockEntity.class)
public abstract class SteamerBlockEntityMixin {
    @ModifyVariable(method = "cookingTick", at = @At("STORE"), name = "resultStack")
    private ItemStack quality_food$applyQuality(final ItemStack result, @Local(name = "stack") final ItemStack ingredient) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(ingredient));
        return result;
    }
}
