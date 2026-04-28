package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.common.block.PieBlock;

@Mixin(value = PieBlock.class, remap = false)
public abstract class PieBlockMixin {
    @ModifyVariable(method = "consumeBite", at = @At("STORE"), name = "sliceStack")
    public ItemStack quality_food$applyQualityToBite(final ItemStack slice, final Level level, final BlockPos position) {
        QualityUtils.applyQuality(slice, LevelData.get(level, position));
        return slice;
    }

    @ModifyArg(method = "cutSlice", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), index = 1)
    public ItemStack quality_food$applyQualityToSlice(final ItemStack stack, @Local(argsOnly = true) final Level level, @Local(argsOnly = true) final BlockPos position) {
        QualityUtils.applyQuality(stack, LevelData.get(level, position));
        return stack;
    }
}
