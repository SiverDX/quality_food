package de.cadentem.quality_food.compat.jade;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.registry.QFComponents;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.impl.ui.ElementHelper;

import java.util.Optional;

public class QualityProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {
    private static final ResourceLocation ID = QualityFood.location("quality");
    private static final Vec2 TRANSLATE = new Vec2(2, 0.5f);

    @Override
    public void appendTooltip(final ITooltip toolTip, final BlockAccessor accessor, final IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();

        if (tag.contains(QualityFood.concat("type"))) {
            int level = tag.getInt(QualityFood.concat("level"));

            if (level <= 0) {
                return;
            }

            ResourceLocation location = ResourceLocation.parse(tag.getString(QualityFood.concat("type")));

            toolTip.add(Component.translatable(QualityFood.concat("quality")));
            toolTip.append(Component.translatable("quality_type." + location.toLanguageKey()));

            Optional<Holder.Reference<QualityType>> optional = accessor.getLevel().holder(ResourceKey.create(QFComponents.QUALITY_TYPE_REGISTRY, location));
            optional.ifPresent(qualityTypeReference -> toolTip.append(ElementHelper.INSTANCE.sprite(qualityTypeReference.value().icon(), 10, 10).translate(TRANSLATE)));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public void appendServerData(final CompoundTag tag, final BlockAccessor accessor) {
        Quality quality = accessor.getLevel().getData(AttachmentHandler.LEVEL_DATA).get(accessor.getPosition());

        if (quality != Quality.NONE) {
            tag.putString(QualityFood.concat("type"), quality.type().toString());
            tag.putInt(QualityFood.concat("level"), quality.level());
        }
    }

    @Override
    public boolean shouldRequestData(final BlockAccessor accessor) {
        return Utils.isValidBlock(accessor.getBlock());
    }
}
