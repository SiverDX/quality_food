package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public class BlockMixin {
    @ModifyVariable(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"), argsOnly = true)
    private static ItemStack food_quality$applyQuality(final ItemStack stack, /* Method arguments: */ final Level level) {
        if (stack.is(Items.GLOW_BERRIES)) {
            QualityUtils.applyQuality(stack, level);
        }

        return stack;
    }
}
