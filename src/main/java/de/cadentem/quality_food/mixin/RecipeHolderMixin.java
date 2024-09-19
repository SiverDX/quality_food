package de.cadentem.quality_food.mixin;

import de.cadentem.quality_food.util.RecipeExtension;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RecipeHolder.class)
public abstract class RecipeHolderMixin implements RecipeExtension {
    @Unique @Nullable private QualityFoodStatus quality_food$status = QualityFoodStatus.NOT_INITIALIZED;

    @Override
    public @Nullable QualityFoodStatus quality_food$getStatus() {
        return quality_food$status;
    }

    @Override
    public void quality_food$setStatus(@Nullable final QualityFoodStatus status) {
        this.quality_food$status = status;
    }
}
