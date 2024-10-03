package de.cadentem.quality_food.mixin.smarterfarmers;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.mehvahdjukaar.smarterfarmers.SFHarvestFarmland;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SFHarvestFarmland.class, remap = false)
public abstract class SFHarvestFarmlandMixin {
    @Shadow public BlockPos aboveFarmlandPos;

    @Inject(method = "replant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE))
    private void quality_food$storeQuality(final ServerLevel level, final Villager villager, final Item item, final CallbackInfo callback, @Local final ItemStack seed) {
        Quality quality = QualityUtils.getQuality(seed);

        if (quality.level() > 0) {
            LevelData.set(level, aboveFarmlandPos, quality);
        }
    }
}
