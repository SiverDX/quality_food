package de.cadentem.quality_food.mixin.fruitfulfun;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import snownee.fruits.food.FeastBlock;

/** Apply quality to served item */
@Mixin(FeastBlock.class)
public abstract class FeastBlockMixin {
    @ModifyVariable(method = "use", at = @At("STORE"), ordinal = 0)
    private ItemStack quality_food$applyQuality(final ItemStack serving, @Local(argsOnly = true) final Level level, @Local(argsOnly = true) final BlockPos position) {
        Quality quality = LevelData.get(level, position, true);

        if (quality.level() > 0) {
            QualityUtils.applyQuality(serving, quality);
        }

        return serving;
    }
}
