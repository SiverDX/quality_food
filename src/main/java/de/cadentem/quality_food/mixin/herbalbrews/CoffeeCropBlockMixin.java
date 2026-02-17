package de.cadentem.quality_food.mixin.herbalbrews;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.core.attachments.LevelData;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.herbalbrews.core.blocks.CoffeeCropBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CoffeeCropBlock.class)
public abstract class CoffeeCropBlockMixin {
    // FIXME
//    @ModifyArg(method = "use", at = @At(value = "INVOKE", target = "Lnet/satisfy/herbalbrews/core/blocks/CoffeeCropBlock;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"))
//    private ItemStack quality_food$applyQuality(final ItemStack bean, @Local(argsOnly = true) final Level level, @Local(argsOnly = true) final BlockPos position) {
//        Quality quality = LevelData.get(level, position, true);
//
//        if (QualityUtils.isValidQuality(quality)) {
//            QualityUtils.applyQuality(bean, quality);
//        }
//
//        return bean;
//    }
}
