package de.cadentem.quality_food.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.registry.QFComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record Quality(ResourceLocation type, int level, Optional<List<FoodProperties.PossibleEffect>> effects) {
    public static final Quality NONE = new Quality(QualityFood.location("none"), 0, Optional.empty());
    public static final Quality PLAYER_PLACED = new Quality(QualityFood.location("player_placed"), -1, Optional.empty());

    public static final Codec<Quality> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    ResourceLocation.CODEC.fieldOf("type").forGetter(Quality::type),
                    Codec.INT.fieldOf("level").forGetter(Quality::level),
                    FoodProperties.PossibleEffect.CODEC.listOf().optionalFieldOf("effects").forGetter(Quality::effects))
            .apply(builder, Quality::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Quality> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, Quality::type,
            ByteBufCodecs.INT, Quality::level,
            ByteBufCodecs.optional(FoodProperties.PossibleEffect.STREAM_CODEC.apply(ByteBufCodecs.list())), Quality::effects,
            Quality::new
    );

    /** May return a direct holder of {@link QualityType#NONE} */
    public Holder<QualityType> getType() {
        if (this == NONE) {
            return Holder.direct(QualityType.NONE);
        }

        HolderLookup.RegistryLookup<QualityType> lookup = CommonHooks.resolveLookup(QFComponents.QUALITY_TYPE_REGISTRY);

        if (lookup == null) {
            return Holder.direct(QualityType.NONE);
        }

        Optional<Holder.Reference<QualityType>> optional = lookup.get(QFComponents.key(type));

        if (optional.isPresent()) {
            return optional.get();
        } else {
            return Holder.direct(QualityType.NONE);
        }
    }

    public static Quality getRandom(final ItemStack stack, int level) {
        HolderLookup.RegistryLookup<QualityType> lookup = CommonHooks.resolveLookup(QFComponents.QUALITY_TYPE_REGISTRY);

        if (lookup == null) {
            return Quality.NONE;
        }

        List<Holder<QualityType>> types = new ArrayList<>();

        for (Holder.Reference<QualityType> type : lookup.listElements().toList()) {
            if (type.value().level() == level) {
                types.add(type);
            }
        }

        if (types.isEmpty()) {
            return Quality.NONE;
        }

        Collections.shuffle(types);
        return QualityType.createQuality(types.getFirst(), stack);
    }
}
