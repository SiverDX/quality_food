package de.cadentem.quality_food.mixin.smarterfarmers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.LevelData;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.mehvahdjukaar.smarterfarmers.SFHarvestFarmland;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = SFHarvestFarmland.class, remap = false)
public abstract class SFHarvestFarmlandMixin {
    @Shadow public BlockPos aboveFarmlandPos;

    /** Planted crops retains quality of used seed */
    @Inject(method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/npc/Villager;J)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE))
    private void quality_food$storeQuality(final ServerLevel level, final Villager villager, long tickCount, final CallbackInfo callback, @Local final ItemStack seed) {
        Quality quality = QualityUtils.getQuality(seed);

        if (quality.level() > 0) {
            LevelData.set(level, aboveFarmlandPos, quality);
        }
    }

    /** Highest quality seed is prioritized */
    @WrapOperation(method = "getSeedToPlantAt", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <K, V> V quality_food$keepHighestQualitySeed(final Map<K, V> inventory, final K key, final V value, final Operation<V> original) {
        if (inventory.get(key) instanceof ItemStack inventoryStack && value instanceof ItemStack stack) {
            // Keep the higher quality seed in the "inventory"
            if (QualityUtils.getQuality(inventoryStack).level() >= QualityUtils.getQuality(stack).level()) {
                return null;
            }
        }

        return original.call(inventory, key, value);
    }
}
