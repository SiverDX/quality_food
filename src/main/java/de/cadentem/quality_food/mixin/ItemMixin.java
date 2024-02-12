package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.FoodUtils;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {
    /**
     * Fallback for unsupported containers
     */
    @Inject(method = "onCraftedBy", at = @At("HEAD"))
    private void food_quality$applyQuality(final ItemStack stack, final Level level, final Player player, final CallbackInfo callback) {
        QualityUtils.applyQuality(stack, player);
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(final ItemStack stack, @Nullable final LivingEntity entity) {
        FoodProperties foodProperties = IForgeItem.super.getFoodProperties(stack, entity);

        if (foodProperties != null && QualityUtils.hasQuality(stack)) {
            return FoodUtils.calculateFoodProperties(stack, foodProperties);
        }

        return foodProperties;
    }
}
