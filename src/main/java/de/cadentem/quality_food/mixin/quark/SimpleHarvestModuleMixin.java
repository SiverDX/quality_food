package de.cadentem.quality_food.mixin.quark;

import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.DropData;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vazkii.quark.content.tweaks.module.SimpleHarvestModule;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// TODO :: shrink item stack (seed) with the proper quality and if not present place block with proper quality of shrunk item stack
@Mixin(value = SimpleHarvestModule.class, remap = false)
public abstract class SimpleHarvestModuleMixin {
    /** Roll quality with more context */
    @Inject(method = "harvestAndReplant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;", shift = At.Shift.BEFORE, remap = true))
    private static void quality_food$setDropData(final Level level, final BlockPos position, final BlockState state, final LivingEntity livingEntity, final InteractionHand hand, final CallbackInfoReturnable<Boolean> callback) {
        DropData.current.set(new DropData(state, livingEntity instanceof Player player ? player : null, level.getBlockState(position.below())));
    }

    /** Set default quality state to bypass config */
    @ModifyArg(method = "getAction", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
    private static Object quality_food$allowReplant(final Object object) {
        if (!ServerConfig.QUARK_HANDLE_CONFIG.get()) {
            return object;
        }

        if (object instanceof BlockState state && state.hasProperty(Utils.QUALITY_STATE)) {
            return state.setValue(Utils.QUALITY_STATE, Quality.NONE.ordinal());
        }

        return object;
    }

    /** Set default quality state to bypass config */
    @ModifyArg(method = "harvestAndReplant", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object quality_food$getProperState(final Object object) {
        if (!ServerConfig.QUARK_HANDLE_CONFIG.get()) {
            return object;
        }

        if (object instanceof BlockState state && state.hasProperty(Utils.QUALITY_STATE)) {
            return state.setValue(Utils.QUALITY_STATE, Quality.NONE.ordinal());
        }

        return object;
    }
}
