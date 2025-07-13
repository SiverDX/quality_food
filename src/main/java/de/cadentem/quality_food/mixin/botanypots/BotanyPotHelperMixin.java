package de.cadentem.quality_food.mixin.botanypots;

import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.QualityUtils;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.events.BotanyPotEventDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(value = BotanyPotHelper.class, remap = false)
public abstract class BotanyPotHelperMixin {

    @Shadow
    @Final
    public static BotanyPotEventDispatcher EVENT_DISPATCHER;

    @Inject(method = "generateDrop", at = @At(value = "HEAD"), cancellable = true)
    private static void quality_food$applyQuality(Random rng, Level level, BlockPos pos, BlockEntityBotanyPot pot, Crop crop, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> original = crop.generateDrops(rng, level, pos, pot);
        List<ItemStack> newItems = new ArrayList<>();

        ItemStack cropItem = pot.getInventory().getCropStack();
        ItemStack soil = pot.getInventory().getSoilStack();

        for (ItemStack item : original) {
            QualityUtils.applyQuality(item, cropItem, soil);
            newItems.add(item);
        }

        cir.setReturnValue(EVENT_DISPATCHER.postCropDrops(rng, level, pos, pot, crop, newItems));
        cir.cancel();
    }

}
