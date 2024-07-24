package de.cadentem.quality_food.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.food.FoodProperties;

public record ChanceEffect(FoodProperties.PossibleEffect effect, double chance) {
    public static Codec<ChanceEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    FoodProperties.PossibleEffect.CODEC.fieldOf("effect").forGetter(ChanceEffect::effect),
                    Codec.DOUBLE.optionalFieldOf("chance", 1d).forGetter(ChanceEffect::chance))
            .apply(builder, ChanceEffect::new));
}
