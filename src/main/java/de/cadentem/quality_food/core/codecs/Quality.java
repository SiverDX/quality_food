package de.cadentem.quality_food.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.*;

public record Quality(ResourceLocation type, int level, Optional<List<FoodProperties.PossibleEffect>> effects) {
    public static final Quality NONE = new Quality(QualityFood.location("none"), 0, Optional.empty());
    public static final Quality PLAYER_PLACED = new Quality(QualityFood.location("none"), -1, Optional.empty());

    public static final Codec<Quality> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(Quality::type),
                    Codec.INT.fieldOf("level").forGetter(Quality::level),
                    FoodProperties.PossibleEffect.CODEC.listOf().optionalFieldOf("effects").forGetter(Quality::effects))
            .apply(builder, Quality::new));

    public QualityType getType() {
        if (this == NONE) {
            return QualityType.NONE;
        }

        HolderLookup.RegistryLookup<QualityType> lookup = CommonHooks.resolveLookup(QFComponents.QUALITY_TYPE_REGISTRY);

        if (lookup == null) {
            return QualityType.NONE;
        }

        return lookup.get(QFComponents.key(type)).map(Holder.Reference::value).orElse(QualityType.NONE);
    }

    public static Quality getRandom(final ItemStack stack, int level) {
        Registry<QualityType> registry = Utils.getQualityRegistry();

        if (registry == null) {
            return Quality.NONE;
        }

        List<QualityType> types = new ArrayList<>();

        for (Map.Entry<ResourceKey<QualityType>, QualityType> entry : registry.entrySet()) {
            QualityType type = entry.getValue();

            if (type.level() == level) {
                types.add(type);
            }
        }

        if (types.isEmpty()) {
            return Quality.NONE;
        }

        Collections.shuffle(types);
        return types.getFirst().createQuality(stack);
    }
}
