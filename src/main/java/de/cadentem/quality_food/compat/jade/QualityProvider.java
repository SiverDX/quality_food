package de.cadentem.quality_food.compat.jade;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.OverlayUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class QualityProvider implements IBlockComponentProvider {
    private static final ResourceLocation ID = new ResourceLocation(QualityFood.MODID, "quality");
    private static final Vec2 SIZE = new Vec2(10, 10);
    private static final Vec2 TRANSLATE = new Vec2(2, 0.5f);

    @Override
    public void appendTooltip(final ITooltip toolTip, final BlockAccessor accessor, final IPluginConfig config) {
        CompoundTag tag = accessor.getServerData();

        if (tag.contains(QualityFood.concat("ordinal"))) {
            Quality quality = Quality.get(tag.getInt(QualityFood.concat("ordinal")));

            if (quality.level() > 0) {
                toolTip.add(Component.translatable("quality_food.quality"));
                toolTip.append(Component.literal(quality.getName()));
                toolTip.append(toolTip.getElementHelper().item(OverlayUtils.getOverlay(quality), 0.5f).size(SIZE).translate(TRANSLATE));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
