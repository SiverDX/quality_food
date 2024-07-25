package de.cadentem.quality_food.compat.jade;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.attachments.AttachmentHandler;
import de.cadentem.quality_food.core.codecs.Quality;
import de.cadentem.quality_food.core.codecs.QualityType;
import de.cadentem.quality_food.util.Utils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.impl.ui.ElementHelper;

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
            toolTip.append(Component.literal(I18n.get("quality_type." + location.toLanguageKey())));

            Registry<QualityType> registry = Utils.getQualityRegistry();

            if (registry != null) {
                QualityType type = registry.get(location);

                if (type != null) {
                    toolTip.append(ElementHelper.INSTANCE.sprite(type.icon(), 10, 10).translate(TRANSLATE));
                }
            }
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
