package de.cadentem.quality_food.core.jade;

import de.cadentem.quality_food.QualityFood;
import de.cadentem.quality_food.core.Quality;
import de.cadentem.quality_food.util.OverlayUtils;
import de.cadentem.quality_food.util.Utils;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class QualityProvider implements IComponentProvider {
    private static final ResourceLocation ID = new ResourceLocation(QualityFood.MODID, "quality");
    private static final Vec2 SIZE = new Vec2(10, 10);
    private static final Vec2 TRANSLATE = new Vec2(2, 0.5f);

    @Override
    public void appendTooltip(final ITooltip toolTip, final BlockAccessor accessor, final IPluginConfig config) {
        if (accessor.getBlockState().hasProperty(Utils.QUALITY_STATE)) {
            Quality quality = Quality.get(accessor.getBlockState().getValue(Utils.QUALITY_STATE));

            if (quality.level() > 0) {
                toolTip.add(new TranslatableComponent("quality_food.quality"));
                toolTip.append(new TextComponent(quality.getName()));
                toolTip.append(toolTip.getElementHelper().item(OverlayUtils.getOverlay(quality), 0.5f).size(SIZE).translate(TRANSLATE));
            }
        }
    }
}
