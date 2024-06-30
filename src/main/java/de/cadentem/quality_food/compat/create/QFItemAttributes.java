package de.cadentem.quality_food.compat.create;

import com.simibubi.create.content.logistics.filter.ItemAttribute;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.QualityUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class QFItemAttributes implements ItemAttribute {
    private static final String ID = "quality_food.quality";
    private static final QFItemAttributes EMPTY = new QFItemAttributes(null);

    private final Quality quality;

    public QFItemAttributes(final Quality quality) {
        this.quality = quality;
    }

    public static void register() {
        ItemAttribute.register(EMPTY);
    }

    @Override
    public boolean appliesTo(final ItemStack stack) {
        return quality == QualityUtils.getQuality(stack);
    }

    @Override
    public List<ItemAttribute> listAttributesOf(final ItemStack stack) {
        if (QualityUtils.hasQuality(stack)) {
            return List.of(new QFItemAttributes(QualityUtils.getQuality(stack)));
        }

        return List.of();
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
    public void writeNBT(final CompoundTag tag) {
        if (quality != null) {
            tag.putInt(ID, quality.ordinal());
        }
    }

    @Override
    public ItemAttribute readNBT(final CompoundTag tag) {
        if (tag.contains(ID)) {
            Quality quality = Quality.get(tag.getInt(ID));
            return quality.level() > 0 ? new QFItemAttributes(quality) : EMPTY;
        }

        return EMPTY;
    }
}
