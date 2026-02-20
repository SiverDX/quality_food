package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CampfireBlockEntity.class)
@Debug(export = true)
public abstract class CampFireBlockEntityMixin {
    @ModifyVariable(method = "cookTick", at = @At(value = "STORE"), ordinal = 1)
    private static ItemStack quality_food$applyQuality(final ItemStack cookedItem, @Local(ordinal = 0) final ItemStack ingredient) {
        QualityUtils.applyQuality(cookedItem, QualityUtils.getQuality(ingredient));
        return cookedItem;
    }
}
