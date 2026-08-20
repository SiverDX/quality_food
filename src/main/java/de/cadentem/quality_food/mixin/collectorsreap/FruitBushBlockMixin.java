package de.cadentem.quality_food.mixin.collectorsreap;

import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.compat.collectorsreap.FruitBushContext;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.DropData;
import de.cadentem.quality_food.util.QualityUtils;
import net.brdle.collectorsreap.common.block.FruitBushBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FruitBushBlock.class)
public abstract class FruitBushBlockMixin {
    @Unique private FruitBushContext quality_food$context;

    /** Set up the context (contains block state and player data) */
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/brdle/collectorsreap/common/block/FruitBushBlock;dropFruit(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", shift = At.Shift.BEFORE, remap = false))
    private void quality_food$storeContext(final BlockState state, final Level level, final BlockPos position, final Player player, final InteractionHand hand, final BlockHitResult result, final CallbackInfoReturnable<InteractionResult> callback) {
        quality_food$context = new FruitBushContext(state, level, position, player);
    }

    /** Clean up the context */
    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/brdle/collectorsreap/common/block/FruitBushBlock;dropFruit(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", shift = At.Shift.AFTER, remap = false))
    private void quality_food$clearContext(final BlockState state, final Level level, final BlockPos position, final Player player, final InteractionHand hand, final BlockHitResult result, final CallbackInfoReturnable<InteractionResult> callback) {
        quality_food$context = null;
    }

    /** Apply quality to the harvested fruit */
    @ModifyVariable(method = "dropFruit", at = @At("STORE"), remap = false, name = "stack")
    private ItemStack quality_food$applyQuality(final ItemStack fruit) {
        if (quality_food$context != null) {
            Quality blockQuality = LevelData.get(quality_food$context.level(), quality_food$context.position());
            QualityUtils.applyHarvestQuality(fruit, quality_food$context.state(), blockQuality, quality_food$context.player(), quality_food$context.level().getBlockState(quality_food$context.position().below()));
            DropData.CURRENT.set(DropData.SKIP);
        }

        return fruit;
    }
}
