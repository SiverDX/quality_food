package de.cadentem.quality_food.util;

import org.jetbrains.annotations.Nullable;

public interface RecipeExtension {
    @Nullable QualityFoodStatus quality_food$getStatus();
    void quality_food$setStatus(@Nullable final QualityFoodStatus status);

    enum QualityFoodStatus {
        NOT_INITIALIZED,
        /** Don't roll for quality */
        NO_QUALITY,
        /** Retain the quality for the result */
        RETAIN_QUALITY,
        /** Don't roll for quality and retain the quality for the result */
        NO_QUALITY_AND_RETAIN_QUALITY
    }
}
