package de.cadentem.quality_food.mixin.farmersdelight;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.RarityUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(CuttingBoardBlockEntity.class)
public class CuttingBoardBlockEntityMixin {
    @ModifyArg(method = "lambda$processStoredItemUsingTool$2", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), index = 1)
    private ItemStack applyRarity(final ItemStack stack, @Local(argsOnly = true) final Player player) {
        if (player.getLevel().isClientSide()) {
            return stack;
        }

        CuttingBoardBlockEntity instance = (CuttingBoardBlockEntity) (Object) this;
        RarityUtils.applyRarity(stack, player.getRandom(), player.getLuck() + RarityUtils.getRarity(instance.getStoredItem()).ordinal() * 3);
        return stack;
    }
}
