package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import vectorwing.farmersdelight.common.block.FeastBlock;

/** Apply quality to served item */
@Mixin(FeastBlock.class)
public abstract class FeastBlockMixin {
    @ModifyVariable(method = "takeServing", at = @At("STORE"), ordinal = 0)
    private ItemStack quality_food$applyQualityToItem(final ItemStack stack, @Local(argsOnly = true) final LevelAccessor level, @Local(argsOnly = true) final BlockPos position) {
        QualityUtils.applyQuality(stack, LevelData.get(level, position));
        return stack;
    }
}
