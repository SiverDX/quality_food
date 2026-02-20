package de.cadentem.quality_food.mixin.kaleidoscope_cookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChoppingBoardBlockEntity.class)
public abstract class ChoppingBoardBlockEntityMixin {
    @Shadow
    private ItemStack result;

    @Inject(method = "onPutItem", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/blockentity/kitchen/ChoppingBoardBlockEntity;refresh()V", shift = At.Shift.BEFORE))
    private void quality_food$applyQuality(final Level level, final LivingEntity user, final ItemStack putOnItem, final CallbackInfoReturnable<Boolean> callback) {
        QualityUtils.applyQuality(result, QualityUtils.getQuality(putOnItem));
    }
}
