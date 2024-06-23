package de.cadentem.quality_food.mixin.vinery;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import satisfyu.vinery.block.grape.GrapeBush;

@Mixin(GrapeBush.class)
public abstract class GrapeBushMixin {
    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lsatisfyu/vinery/block/grape/GrapeBush;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
    private ItemStack quality_food$applyQuality(final ItemStack stack, @Local(argsOnly = true) final BlockState state, @Local(argsOnly = true) final Player player, @Local(argsOnly = true) final BlockPos position) {
        QualityUtils.applyQuality(stack, state, player, player.getLevel().getBlockState(position.below()));
        return stack;
    }
}
