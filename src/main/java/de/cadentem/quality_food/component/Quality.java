package de.cadentem.quality_food.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public record Quality(ResourceLocation type, int level, Optional<List<FoodProperties.PossibleEffect>> effects) {
    public static final Codec<Quality> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(Quality::type),
                    Codec.INT.fieldOf("level").forGetter(Quality::level),
                    FoodProperties.PossibleEffect.CODEC.listOf().optionalFieldOf("effects").forGetter(Quality::effects))
            .apply(builder, Quality::new));

    public @Nullable QualityType getType(@Nullable final RegistryAccess access) {
        if (access == null) {
            return null;
        }

        return access.registryOrThrow(QFRegistries.QUALITY_TYPE_REGISTRY).get(type);
    }
}
