package de.cadentem.quality_food.mixin.create;

import com.simibubi.create.content.kinetics.millstone.MillstoneBlockEntity;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MillstoneBlockEntity.class)
public abstract class MillstoneBlockEntityMixin {
    @Unique
    private static final ThreadLocal<Quality> quality_food$INPUT = new ThreadLocal<>();

    @ModifyVariable(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE))
    private ItemStack quality_food$storeInput(final ItemStack stack) {
        quality_food$INPUT.set(QualityUtils.getQuality(stack));
        return stack;
    }

    @ModifyArg(method = "lambda$process$1", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/ItemHandlerHelper;insertItemStacked(Lnet/minecraftforge/items/IItemHandler;Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/item/ItemStack;"), remap = false)
    private ItemStack quality_food$applyQuality(final ItemStack stack) {
        QualityUtils.applyQuality(stack, quality_food$INPUT.get());
        return stack;
    }
}
