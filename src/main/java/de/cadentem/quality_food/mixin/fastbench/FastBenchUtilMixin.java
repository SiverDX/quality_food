package de.cadentem.quality_food.mixin.fastbench;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.config.ServerConfig;
import de.cadentem.quality_food.core.Bonus;
import de.cadentem.quality_food.util.QualityUtils;
import dev.shadowsoffire.fastbench.util.CraftingInventoryExt;
import dev.shadowsoffire.fastbench.util.FastBenchUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = FastBenchUtil.class, remap = false)
public abstract class FastBenchUtilMixin {
    @ModifyReturnValue(method = "handleShiftCraft(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/inventory/AbstractContainerMenu;Lnet/minecraft/world/inventory/Slot;Ldev/shadowsoffire/fastbench/util/CraftingInventoryExt;Lnet/minecraft/world/inventory/ResultContainer;Ldev/shadowsoffire/fastbench/util/FastBenchUtil$OutputMover;)Lnet/minecraft/world/item/ItemStack;", at = @At("RETURN"))
    private static ItemStack quality_food$applyQuality(final ItemStack result, @Local(argsOnly = true) final Player player, @Local(argsOnly = true) final CraftingInventoryExt craftSlots, @Local(argsOnly = true) final ResultContainer resultSlots) {
        if (ServerConfig.isNoQualityRecipe(resultSlots.getRecipeUsed())) {
            return result;
        }

        QualityUtils.applyQuality(result, player, Bonus.additive(QualityUtils.getQualityBonus(craftSlots)));
        return result;
    }

    @ModifyVariable(method = "slotChangedCraftingGrid", at = @At(value = "STORE", ordinal = 1))
    private static ItemStack quality_food$handleConversion(final ItemStack result, @Local(argsOnly = true) final CraftingInventoryExt craftSlots, @Local(argsOnly = true) final ResultContainer resultSlots) {
        QualityUtils.handleConversion(result, craftSlots, resultSlots.getRecipeUsed());
        return result;
    }
}