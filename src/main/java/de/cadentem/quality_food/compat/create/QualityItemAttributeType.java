package de.cadentem.quality_food.compat.create;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QualityItemAttributeType implements ItemAttributeType {
    public static final DeferredRegister<ItemAttributeType> REGISTRY = DeferredRegister.create(CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE.key(), QualityFood.MODID);
    public static final RegistryObject<ItemAttributeType> TYPE = REGISTRY.register("quality", QualityItemAttributeType::new);

    @Override
    public @NotNull ItemAttribute createAttribute() {
        return new QualityItemAttribute(Quality.NONE);
    }

    @Override
    public List<ItemAttribute> getAllAttributes(final ItemStack stack, final Level level) {
        if (QualityUtils.hasQuality(stack)) {
            return List.of(new QualityItemAttribute(QualityUtils.getQuality(stack)));
        }

        return List.of();
    }

    public static class QualityItemAttribute implements ItemAttribute {
        private static final String ID = QualityFood.MODID + ".quality";
        private Quality quality;

        public QualityItemAttribute(final Quality quality) {
            this.quality = quality;
        }

        @Override
        public boolean appliesTo(final ItemStack stack, final Level level) {
            return quality == QualityUtils.getQuality(stack);
        }

        @Override
        public ItemAttributeType getType() {
            return TYPE.get();
        }

        @Override
        public String getTranslationKey() {
            return ID;
        }

        @Override
        public Object[] getTranslationParameters() {
            if (quality == null) {
                return new Object[]{ComponentContents.EMPTY};
            }

            String name = quality.getName();

            ChatFormatting color = switch (quality) {
                case IRON -> ChatFormatting.WHITE;
                case GOLD -> ChatFormatting.GOLD;
                case DIAMOND -> ChatFormatting.AQUA;
                default -> ChatFormatting.DARK_GRAY;
            };

            return new Object[]{Component.literal(name).withStyle(color)};
        }

        @Override
        public void save(final CompoundTag tag) {
            if (quality != null) {
                tag.putInt(ID, quality.ordinal());
            }
        }

        @Override
        public void load(final CompoundTag tag) {
            Quality quality = Quality.get(tag.getInt(ID));
            this.quality = quality.level() > 0 ? quality : Quality.NONE;
        }
    }
}
