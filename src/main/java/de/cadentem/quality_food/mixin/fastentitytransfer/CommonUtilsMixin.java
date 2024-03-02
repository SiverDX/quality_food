package de.cadentem.quality_food.mixin.fastentitytransfer;

import com.christofmeg.fastentitytransfer.CommonUtils;
import com.google.common.util.concurrent.AtomicDouble;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.capability.BlockDataProvider;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(CommonUtils.class)
public class CommonUtilsMixin {
    @ModifyArg(method = "doLeftClickInteractions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"))
    private static ItemStack quality_food$applyQuality(final ItemStack output, @Local final AbstractFurnaceBlockEntity furnace) {
        if (furnace.getLevel() == null) {
            return output;
        }

        AtomicDouble bonus = new AtomicDouble(0);
        BlockDataProvider.getCapability(furnace).ifPresent(data -> bonus.set(data.useQuality()));
        QualityUtils.applyQuality(output, Bonus.additive(bonus.floatValue()));
        return output;
    }
}
