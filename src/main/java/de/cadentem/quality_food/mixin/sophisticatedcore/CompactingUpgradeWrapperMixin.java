package de.cadentem.quality_food.mixin.sophisticatedcore;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.upgrades.compacting.CompactingUpgradeWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CompactingUpgradeWrapper.class)
public abstract class CompactingUpgradeWrapperMixin {
    @SuppressWarnings("ConstantConditions")
    @ModifyExpressionValue(method = "compactSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z"))
    private boolean quality_food$allowQualityTag(boolean hasTag, @Local final ItemStack stack) {
        if (hasTag) {
            CompoundTag tag = stack.getTag();

            if (QualityUtils.hasQuality(stack) && tag.getAllKeys().size() == 1) {
                return false;
            }
        }

        return hasTag;
    }

    @ModifyVariable(method = "tryCompacting", at = @At(value = "STORE"), ordinal = 1, remap = false)
    private ItemStack quality_food$handleConversion(final ItemStack compacted, @Local(ordinal = 0) final ItemStack ingredient) {
        QualityUtils.applyQuality(compacted, QualityUtils.getQuality(ingredient));
        return compacted;
    }
}
