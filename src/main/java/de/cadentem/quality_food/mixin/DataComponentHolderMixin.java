package de.cadentem.quality_food.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import de.cadentem.quality_food.util.FoodUtils;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.ConsumableListener;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.stream.Stream;

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

    /** To handle {@link Consumable#onConsume(Level, LivingEntity, ItemStack)} */
    @ModifyReturnValue(method = "getAllOfType", at = @At("RETURN"))
    private <T> Stream<T> quality_food$modifyFood(final Stream<T> original, @Local(argsOnly = true) final Class<? extends T> valueClass) {
        //noinspection ConstantValue -> not always false
        if (!((Object) this instanceof ItemStack stack)) {
            return original;
        }

        if (valueClass == ConsumableListener.class) {
            return original.map(listener -> {
                if (listener instanceof FoodProperties properties) {
                    //noinspection unchecked -> valid
                    return (T) FoodUtils.handleFoodProperties(stack, properties);
                }

                return listener;
            });
        }

        return original;
    }
}
