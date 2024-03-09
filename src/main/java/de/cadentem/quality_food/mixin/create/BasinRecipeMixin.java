package de.cadentem.quality_food.mixin.create;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(value = BasinRecipe.class, remap = false)
public abstract class BasinRecipeMixin {
    @Unique
    private static final ThreadLocal<Quality> quality_food$INPUT = new ThreadLocal<>();

    @ModifyVariable(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/items/IItemHandler;extractItem(IIZ)Lnet/minecraft/world/item/ItemStack;", ordinal = 0, shift = At.Shift.BY, by = 2))
    private static ItemStack quality_food$storeInput(final ItemStack stack) {
        Quality quality = QualityUtils.getQuality(stack);

        if (quality_food$INPUT.get() == null || quality.level() > quality_food$INPUT.get().level()) {
            quality_food$INPUT.set(quality);
        }

        return stack;
    }

    @ModifyArg(method = "apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;Z)Z", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;acceptOutputs(Ljava/util/List;Ljava/util/List;Z)Z"), index = 0)
    private static List<ItemStack> quality_food$applyQuality(final List<ItemStack> stacks) {
        stacks.forEach(stack -> QualityUtils.applyQuality(stack, quality_food$INPUT.get()));
        quality_food$INPUT.remove();
        return stacks;
    }
}
