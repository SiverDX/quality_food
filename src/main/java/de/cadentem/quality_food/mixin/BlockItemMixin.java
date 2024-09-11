package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.capability.LevelDataProvider;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Store quality for placed blocks */
@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(method = "placeBlock", at = @At("RETURN"))
    private void quality_food$storeQuality(final BlockPlaceContext context, final BlockState state, final CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValue() && Utils.isValidBlock(state)) {
            LevelDataProvider.getCapability(context.getLevel()).ifPresent(data -> {
                Quality quality = QualityUtils.getQuality(context.getItemInHand());
                data.set(context.getClickedPos(), quality != Quality.NONE ? quality : Quality.NONE_PLAYER_PLACED);
            });
        }
    }
}
