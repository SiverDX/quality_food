package de.cadentem.quality_food.mixin.create;

import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MillstoneBlockEntity.class)
public abstract class MillstoneBlockEntityMixin {
    @Unique private Quality quality_food$quality = Quality.NONE;

    @ModifyVariable(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE), name = "stackInSlot")
    private ItemStack quality_food$storeInput(final ItemStack stack) {
        quality_food$quality = QualityUtils.getQuality(stack);
        return stack;
    }

    @ModifyArg(method = "lambda$process$1", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/items/ItemHandlerHelper;insertItemStacked(Lnet/neoforged/neoforge/items/IItemHandler;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;"), remap = false)
    private ItemStack quality_food$applyQuality(final ItemStack stack) {
        QualityUtils.applyQuality(stack, quality_food$quality);
        return stack;
    }
}