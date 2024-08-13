package de.cadentem.quality_food.mixin.sophisticatedcore;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CompactingUpgradeWrapper.class)
public abstract class CompactingUpgradeWrapperMixin {
    /** Allow compacting when only quality data is present */
    @ModifyExpressionValue(method = "compactSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getComponentsPatch()Lnet/minecraft/core/component/DataComponentPatch;"))
    private DataComponentPatch quality_food$allowQualityTag(final DataComponentPatch original, @Local final ItemStack stack) {
        if (original.size() == 1 && QualityUtils.hasQuality(stack)) {
            return DataComponentPatch.EMPTY;
        }

        return original;
    }

    /** Retain quality when compacting */
    @ModifyVariable(method = "tryCompacting", at = @At(value = "STORE"), ordinal = 1, remap = false)
    private ItemStack quality_food$handleConversion(final ItemStack compacted, @Local(ordinal = 0) final ItemStack ingredient) {
        QualityUtils.applyQuality(compacted, QualityUtils.getQuality(ingredient));
        return compacted;
    }
}
