package de.cadentem.quality_food.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

import java.util.List;
import java.util.Optional;

public record Quality(ResourceLocation type, int level, Optional<List<FoodProperties.PossibleEffect>> effects) {
    public static final Quality NONE = new Quality(QualityFood.location("none"), 0, Optional.empty());

    public static final Codec<Quality> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(Quality::type),
                    Codec.INT.fieldOf("level").forGetter(Quality::level),
                    FoodProperties.PossibleEffect.CODEC.listOf().optionalFieldOf("effects").forGetter(Quality::effects))
            .apply(builder, Quality::new));

    public QualityType getType() {
        Registry<QualityType> registry = Utils.getQualityRegistry();

        if (registry == null || this == NONE) {
            return QualityType.NONE;
        }

        QualityType qualityType = registry.get(type);

        if (qualityType == null) {
            return QualityType.NONE;
        }

        return qualityType;
    }
}
