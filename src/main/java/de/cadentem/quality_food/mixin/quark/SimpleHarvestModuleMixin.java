package de.cadentem.quality_food.mixin.quark;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.violetmoon.quark.content.tweaks.module.SimpleHarvestModule;

// TODO :: shrink item stack (seed) with the proper quality and if not present place block with proper quality of shrunk item stack
/** Call the methods with the default quality state instead of the actual one */
@Mixin(value = SimpleHarvestModule.class, remap = false)
@Debug(export = true)
public abstract class SimpleHarvestModuleMixin {
    @ModifyArg(method = "getActionForBlock", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
    private static Object quality_food$allowReplant(final Object object) {
        if (!ServerConfig.QUARK_HANDLE_CONFIG.get()) {
            return object;
        }

        if (object instanceof BlockState state && state.hasProperty(Utils.QUALITY_STATE)) {
            return state.setValue(Utils.QUALITY_STATE, Quality.NONE.ordinal());
        }

        return object;
    }

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

    @ModifyArg(method = "harvestAndReplant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    private static BlockState quality_food$retainQuality(final BlockState state, @Local(argsOnly = true) BlockState currentState) {
        if (!ServerConfig.QUARK_HANDLE_CONFIG.get()) {
            return state;
        }

        if (state.hasProperty(Utils.QUALITY_STATE) && currentState.hasProperty(Utils.QUALITY_STATE)) {
            return state.setValue(Utils.QUALITY_STATE, currentState.getValue(Utils.QUALITY_STATE));
        }

        return state;
    }

    /* Loads too early for the server config
    @Redirect(method = "lambda$configChanged$3", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <K, V> V quality_food$handleConfigRegistry(final Map<K, V> instance, final K key, final V value) {
        return quality_food$fillMap(instance, key, value);
    }

    @Redirect(method = "configChanged", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <K, V> V quality_food$handleConfigFile(final Map<K, V> instance, final K key, final V value) {
        return quality_food$fillMap(instance, key, value);
    }

    @Unique
    @SuppressWarnings("unchecked")
    private <K, V> @Nullable V quality_food$fillMap(final Map<K, V> instance, final K key, final V value) {
        for (Quality quality : Quality.values()) {
            instance.put((K) ((BlockState) key).setValue(Utils.QUALITY_STATE, quality.ordinal()), (V) ((BlockState) value).setValue(Utils.QUALITY_STATE, quality.ordinal()));
        }

        return null;
    }
    */
}
