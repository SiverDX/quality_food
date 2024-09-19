package de.cadentem.quality_food.mixin.farm_and_charm;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.block.crops.StrawberryCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(StrawberryCropBlock.class)
public abstract class StrawberryCropBlockMixin {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/satisfy/farm_and_charm/block/crops/StrawberryCropBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack quality_food$applyQuality(final ItemStack stack, @Local(argsOnly = true) final BlockState state, @Local(argsOnly = true) final Level level, @Local(argsOnly = true) final BlockPos position, @Local(argsOnly = true) final Player player) {
        QualityUtils.applyQuality(stack, LevelData.get(level, position), state, player, level.getBlockState(position.below()));
        return stack;
    }
}
