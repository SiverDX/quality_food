package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

/** Apply quality to cut items */
@Mixin(value = CuttingBoardBlockEntity.class, remap = false)
public class CuttingBoardBlockEntityMixin {
    @ModifyArg(method = "lambda$processStoredItemUsingTool$2", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), index = 1)
    private ItemStack quality_food$applyQuality(final ItemStack stack, @Local(argsOnly = true) final Player player) {
        CuttingBoardBlockEntity instance = (CuttingBoardBlockEntity) (Object) this;
        QualityUtils.applyQuality(stack, player, QualityUtils.getQuality(instance.getStoredItem()).ordinal());
        return stack;
    }
}
