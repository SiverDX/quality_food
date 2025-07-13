package de.cadentem.quality_food.mixin.botanypots;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(value = BotanyPotHelper.class, remap = false)
public abstract class BotanyPotHelperMixin {

    @ModifyArg(method = "generateDrop", at = @At(value = "INVOKE", target = "Lnet/darkhax/botanypots/events/BotanyPotEventDispatcher;postCropDrops(Ljava/util/Random;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/darkhax/botanypots/block/BlockEntityBotanyPot;Lnet/darkhax/botanypots/data/recipes/crop/Crop;Ljava/util/List;)Ljava/util/List;"))
    private static List<ItemStack> quality_food$applyQuality(final List<ItemStack> items, @Local(argsOnly = true) final BlockEntityBotanyPot pot) {
        ItemStack crop = pot.getInventory().getCropStack();
        ItemStack soil = pot.getInventory().getSoilStack();

        for (ItemStack item : items) {
            QualityUtils.applyQuality(item, crop, soil);
        }

        return items;
    }
}
