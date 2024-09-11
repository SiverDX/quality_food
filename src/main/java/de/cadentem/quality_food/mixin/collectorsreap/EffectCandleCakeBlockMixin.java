package de.cadentem.quality_food.mixin.collectorsreap;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.util.QualityUtils;
import net.brdle.collectorsreap.common.block.EffectCandleCakeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Apply quality to eaten / cut slices */
@Mixin(value = EffectCandleCakeBlock.class, remap = false)
public abstract class EffectCandleCakeBlockMixin {
    @ModifyVariable(method = "eatSlice", at = @At("STORE"))
    private ItemStack quality_food$applyQuality_eat(final ItemStack slice, @Local(argsOnly = true) final Level level, @Local(argsOnly = true) final BlockPos position) {
        QualityUtils.applyQuality(slice, LevelData.get(level, position, true));
        return slice;
    }

    @ModifyArg(method = "cutSlice", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Containers;dropItemStack(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V", remap = true))
    private ItemStack quality_food$applyQuality_cut(final ItemStack slice, @Local(argsOnly = true) Level level, @Local(argsOnly = true) final BlockPos position) {
        QualityUtils.applyQuality(slice, LevelData.get(level, position, true));
        return slice;
    }
}