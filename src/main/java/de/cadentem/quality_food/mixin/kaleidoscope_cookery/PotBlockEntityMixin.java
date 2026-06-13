package de.cadentem.quality_food.mixin.kaleidoscope_cookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotBlockEntity.class)
public abstract class PotBlockEntityMixin {
    @Shadow
    private ItemStack result;

    // TODO :: unsure if there is a good way to consider the quality of the carrier (e.g. cooked rice)
    //         since the quality application happens on input and carrier is only known when result is taken
    @Inject(method = "applyFlexRecipe", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/item/quality/QualityUtils;setQuality(Lnet/minecraft/world/item/ItemStack;Lcom/github/ysbbbbbb/kaleidoscopecookery/item/quality/Quality;)V", shift = At.Shift.BY, by = 2))
    private void quality_food$applyQuality(final Level level, final SimpleInput input, final RecipeHolder<?> recipe, final CallbackInfo callback) {
        QualityUtils.applyQuality(result, input.getInputs(), null, level.registryAccess());
    }
}
