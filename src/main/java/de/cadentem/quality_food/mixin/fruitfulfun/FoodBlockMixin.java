package de.cadentem.quality_food.mixin.fruitfulfun;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import snownee.fruits.food.FoodBlock;

@Mixin(FoodBlock.class)
public abstract class FoodBlockMixin {
    @ModifyVariable(method = "use", at = @At("STORE"))
    private ItemStack quality_food$retainQuality(final ItemStack stack, @Local(argsOnly = true) final BlockState state) {
        if (state.hasProperty(Utils.QUALITY_STATE)) {
            Quality quality = Quality.get(state.getValue(Utils.QUALITY_STATE));

            if (quality.level() > 0) {
                QualityUtils.applyQuality(stack, quality);
            }
        }

        return stack;
    }
}
