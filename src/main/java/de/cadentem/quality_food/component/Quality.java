package de.cadentem.quality_food.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.QualityFood;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.neoforged.neoforge.common.CommonHooks;

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
        if (this == NONE) {
            return QualityType.NONE;
        }

        HolderLookup.RegistryLookup<QualityType> lookup = CommonHooks.resolveLookup(QFRegistries.QUALITY_TYPE_REGISTRY);

        if (lookup == null) {
            return QualityType.NONE;
        }

        return lookup.get(QFRegistries.key(type)).map(Holder.Reference::value).orElse(QualityType.NONE);
    }
}
