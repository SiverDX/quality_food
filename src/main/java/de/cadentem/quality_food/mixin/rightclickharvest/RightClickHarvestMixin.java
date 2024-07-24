package de.cadentem.quality_food.mixin.rightclickharvest;

import de.cadentem.quality_food.attachments.LevelData;
import de.cadentem.quality_food.util.DropData;
import io.github.jamalam360.rightclickharvest.RightClickHarvest;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Roll quality with more context */
@Mixin(value = RightClickHarvest.class, remap = false)
public abstract class RightClickHarvestMixin {
    @Inject(method = "dropStacks", at = @At("HEAD"))
    private static void quality_food$setDropData(final BlockState state, final ServerLevel level, final BlockPos position, final Entity entity, final ItemStack tool, boolean removePlant, final CallbackInfo callback) {
        DropData.current.set(new DropData(LevelData.get(level, position, true), state, entity instanceof Player player ? player : null, level.getBlockState(position.below())));
    }

    @Inject(method = "dropStacks", at = @At("TAIL"))
    private static void quality_food$clearDropDAta(final BlockState state, final ServerLevel level, final BlockPos position, final Entity entity, final ItemStack tool, boolean removePlant, final CallbackInfo callback) {
        DropData.current.remove();
    }
}
