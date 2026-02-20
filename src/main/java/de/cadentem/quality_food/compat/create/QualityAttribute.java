package de.cadentem.quality_food.compat.create;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record QualityAttribute(Quality quality) implements ItemAttribute {
    public static final DeferredRegister<ItemAttributeType> REGISTRY = DeferredRegister.create(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE.key(), QualityFood.MODID);
    public static final DeferredHolder<ItemAttributeType, Type> TYPE = REGISTRY.register("quality", Type::new);

    public static final MapCodec<QualityAttribute> CODEC = Quality.CODEC
            .xmap(QualityAttribute::new, QualityAttribute::quality)
            .fieldOf("value");

    public static final StreamCodec<RegistryFriendlyByteBuf, QualityAttribute> STREAM_CODEC = Quality.STREAM_CODEC
            .map(QualityAttribute::new, QualityAttribute::quality);

    @Override
    public boolean appliesTo(final ItemStack stack, final Level level) {
        return quality != null && quality.getType().equals(QualityUtils.getQuality(stack).getType());
    }

    @Override
    public ItemAttributeType getType() {
        return TYPE.value();
    }

    @Override
    public String getTranslationKey() {
        return QualityFood.MODID + ".quality";
    }

    @Override
    public Object[] getTranslationParameters() {
        Component parameter = Component.empty();

        if (quality != null) {
            parameter = quality.getType().value().name();
        }

        return new Object[]{parameter};
    }

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new QualityAttribute(Quality.NONE);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(final ItemStack stack, final Level level) {
            if (QualityUtils.hasQuality(stack)) {
                return List.of(new QualityAttribute(QualityUtils.getQuality(stack)));
            }

            return List.of();
        }

        @Override
        public MapCodec<? extends ItemAttribute> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAttribute> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
