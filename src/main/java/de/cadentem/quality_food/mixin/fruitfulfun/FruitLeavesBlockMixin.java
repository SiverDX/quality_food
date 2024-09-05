package de.cadentem.quality_food.mixin.fruitfulfun;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import snownee.fruits.block.FruitLeavesBlock;

@Mixin(value = FruitLeavesBlock.class, remap = false)
public abstract class FruitLeavesBlockMixin {
    @ModifyVariable(method = "createItemEntity", at = @At("HEAD"), argsOnly = true)
    private static ItemStack quality_food$retainQuality(final ItemStack fruit, @Local(argsOnly = true) final ServerLevel level, @Local(argsOnly = true) final BlockPos position) {
        BlockState state = level.getBlockState(position);

        if (state.hasProperty(Utils.QUALITY_STATE)) {
            Quality quality = Quality.get(state.getValue(Utils.QUALITY_STATE), true);

            if (quality.level() > 0) {
                QualityUtils.applyQuality(fruit, quality);
            } else if (quality != Quality.NONE_PLAYER_PLACED) {
                QualityUtils.applyQuality(fruit);
            }
        }

        return fruit;
    }

    @ModifyArg(method = "giveItemTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;addItem(Lnet/minecraft/world/item/ItemStack;)Z", remap = true))
    private static ItemStack quality_food$applyQuality(final ItemStack fruit, @Local(argsOnly = true) final Player player, @Local(argsOnly = true) final BlockHitResult result) {
        BlockState state = player.level().getBlockState(result.getBlockPos());

        if (state.hasProperty(Utils.QUALITY_STATE)) {
            Quality quality = Quality.get(state.getValue(Utils.QUALITY_STATE), true);

            if (quality.level() > 0) {
                QualityUtils.applyQuality(fruit, quality);
            } else if (quality != Quality.NONE_PLAYER_PLACED) {
                QualityUtils.applyQuality(fruit, player);
            }
        }

        return fruit;
    }
}
