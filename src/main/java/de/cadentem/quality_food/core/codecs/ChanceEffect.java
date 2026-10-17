package de.cadentem.quality_food.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

public record ChanceEffect(ConsumeEffect effect, double chance) {
    public static Codec<ChanceEffect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    ConsumeEffect.CODEC.fieldOf("effect").forGetter(ChanceEffect::effect),
                    Codec.DOUBLE.optionalFieldOf("chance", 1d).forGetter(ChanceEffect::chance))
            .apply(builder, ChanceEffect::new));
}
