package de.cadentem.quality_food.core.codecs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public record Effect(Optional<HolderSet<Item>> applicableTo, List<ChanceEffect> effects) {
    public static Codec<Effect> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                    RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("applicable_to").forGetter(Effect::applicableTo),
                    ChanceEffect.CODEC.listOf().fieldOf("effects").forGetter(Effect::effects))
            .apply(builder, Effect::new));

    public boolean test(final ItemStack stack) {
        if (stack.getFoodProperties(null) == null) {
            return false;
        }

        return applicableTo.map(stack::is).orElse(true);
    }
}
