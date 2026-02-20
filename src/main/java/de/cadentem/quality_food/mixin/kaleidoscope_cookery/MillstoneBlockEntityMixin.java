package de.cadentem.quality_food.mixin.kaleidoscope_cookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MillstoneBlockEntity.class)
public abstract class MillstoneBlockEntityMixin {
    @Shadow
    private ItemStack output;

    @Shadow
    private ItemStack input;

    @Inject(method = "lambda$tick$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V"))
    private void quality_food$applyQuality(final SingleRecipeInput container, final Level level, final RecipeHolder<?> recipe, final CallbackInfo callback) {
        QualityUtils.applyQuality(output, QualityUtils.getQuality(input));
    }
}
