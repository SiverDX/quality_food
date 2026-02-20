package de.cadentem.quality_food.mixin.kaleidoscope_cookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ShawarmaSpitBlockEntity.class)
public abstract class ShawarmaSpitBlockEntityMixin {
    @Shadow
    public ItemStack cookedItem;

    @ModifyReturnValue(method = "lambda$onPutCookingItem$0", at = @At("RETURN"))
    private Boolean quality_food$applyQuality(final Boolean original, @Local(name = "itemStack") final ItemStack ingredient) {
        if (original) {
            QualityUtils.applyQuality(cookedItem, QualityUtils.getQuality(ingredient));
        }

        return original;
    }
}
