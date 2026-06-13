package de.cadentem.quality_food.mixin.kaleidoscope_cookery;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StockpotBlockEntity.class)
public abstract class StockpotBlockEntityMixin {
    @Shadow
    private ItemStack result;

    @Inject(method = "applyRecipe", at = @At(value = "INVOKE", target = "Lcom/github/ysbbbbbb/kaleidoscopecookery/crafting/recipe/StockpotRecipe;assemble(Lnet/minecraft/world/Container;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.BY, by = 2))
    private void quality_food$applyQuality(final Level level, final StockpotContainer container, final StockpotRecipe recipe, final CallbackInfo callback) {
        QualityUtils.applyQuality(result, container.getItems(), null);
    }
}
