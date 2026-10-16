package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import de.cadentem.quality_food.util.FoodUtils;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponentHolder.class)
public interface DataComponentHolderMixin {
    @SuppressWarnings("unchecked")
    @ModifyReturnValue(method = {"get", "getOrDefault"}, at = @At("RETURN"))
    private <T> T quality_food$modifyFood(final T original, final DataComponentType<? extends T> type) {
        if ((Object) this instanceof ItemStack stack) {
            if (type == DataComponents.FOOD) {
                return (T) FoodUtils.handleFoodProperties(stack, (FoodProperties) original);
            }
            if (type == DataComponents.CONSUMABLE) {
                return (T) FoodUtils.handleConsumable(stack, (Consumable) original);
            }
        }
        return original;
    }
}
