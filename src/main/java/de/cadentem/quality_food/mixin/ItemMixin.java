package de.cadentem.quality_food.mixin;

import com.mojang.datafixers.util.Pair;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.world.effect.MobEffectInstance;
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

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {
    /** Fallback for unsupported containers */
    @Inject(method = "onCraftedBy", at = @At("HEAD"))
    private void food_quality$applyQuality(final ItemStack stack, final Level level, final Player player, final CallbackInfo callback) {
        if (level.isClientSide()) {
            return;
        }

        QualityUtils.applyQuality(stack, player.getRandom(), player.getLuck());
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(final ItemStack stack, @Nullable final LivingEntity entity) {
        FoodProperties foodProperties = IForgeItem.super.getFoodProperties(stack, entity);

        if (foodProperties != null && QualityUtils.hasQuality(stack)) {
            Quality quality = QualityUtils.getQuality(stack);
            int nutrition = (int) (foodProperties.getNutrition() * QualityUtils.getNutritionMultiplier(quality));
            float saturationModifier = (float) (foodProperties.getSaturationModifier() * QualityUtils.getSaturationMultiplier(quality));

            FoodProperties.Builder builder = new FoodProperties.Builder();
            builder.nutrition(nutrition);
            builder.saturationMod(saturationModifier);
            if (foodProperties.isMeat()) builder.meat();
            if (foodProperties.canAlwaysEat()) builder.alwaysEat();
            if (foodProperties.isFastFood()) builder.fast();

            List<Pair<MobEffectInstance, Float>> effects = foodProperties.getEffects();
            effects.forEach(effect -> builder.effect(effect::getFirst, effect.getSecond()));

            return builder.build();
        }

        return foodProperties;
    }
}
