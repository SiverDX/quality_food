package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.FoodUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

/** Handle changes to food properties (i.e. increased nutrition, additional effects, etc.) */
@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {
    @Override
    public @Nullable FoodProperties getFoodProperties(final ItemStack stack, @Nullable final LivingEntity entity) {
        return FoodUtils.handleFoodProperties(stack, IForgeItem.super.getFoodProperties(stack, entity));
    }
}
